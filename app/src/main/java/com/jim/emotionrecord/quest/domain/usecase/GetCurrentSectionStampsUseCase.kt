package com.jim.emotionrecord.quest.domain.usecase

import com.jim.emotionrecord.quest.data.repository.EmotionRecordRepository
import com.jim.emotionrecord.quest.data.repository.QuestUserMetaRepository
import com.jim.emotionrecord.quest.domain.model.DayStamp
import com.jim.emotionrecord.quest.domain.model.Emotion
import com.jim.emotionrecord.quest.domain.model.SectionData
import com.jim.emotionrecord.quest.domain.model.StampState
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCurrentSectionStampsUseCase @Inject constructor(
    private val emotionRecordRepository: EmotionRecordRepository,
    private val questUserMetaRepository: QuestUserMetaRepository,
) {
    suspend operator fun invoke(): SectionData {
        val meta = questUserMetaRepository.get()
        val firstMs = meta?.firstRecordedAt

        val today = LocalDate.now()
        val zone = ZoneId.systemDefault()

        // 첫 기록이 없으면 오늘만 TODAY 상태로 반환
        if (firstMs == null) {
            return SectionData(
                weekNumber = 1,
                sectionStart = today,
                sectionEnd = today.plusDays(6),
                stamps = listOf(DayStamp(today.dayOfWeek.index(), today.dayLabel(), StampState.TODAY, isToday = true)),
                todayRecorded = false,
                todayStampIndex = 0,
            )
        }

        val firstDate = Instant.ofEpochMilli(firstMs).atZone(zone).toLocalDate()
        val daysSinceFirst = ChronoUnit.DAYS.between(firstDate, today).toInt().coerceAtLeast(0)
        val sectionIndex = daysSinceFirst / 7
        val weekNumber = sectionIndex + 1

        val sectionStart = firstDate.plusDays((sectionIndex * 7).toLong())
        val sectionEnd = sectionStart.plusDays(6)

        val stamps = mutableListOf<DayStamp>()
        var todayRecorded = false
        var todayStampIndex: Int? = null

        for (dayIndex in 0..6) {
            val date = sectionStart.plusDays(dayIndex.toLong())
            if (date.isAfter(today)) break   // 미래는 표시 안 함

            val dayStart = date.atStartOfDay(zone).toInstant().toEpochMilli()
            val dayEnd   = date.atTime(23, 59, 59).atZone(zone).toInstant().toEpochMilli()
            val records  = emotionRecordRepository.getInRange(dayStart, dayEnd)
            val isToday  = date == today

            val state: StampState
            val emotion: Emotion?

            if (records.isEmpty()) {
                state   = if (isToday) StampState.TODAY else StampState.SKIPPED
                emotion = null
            } else {
                val record = records.last()
                state   = if (record.missionCompleted) StampState.MISSION else StampState.COMPLETE
                emotion = Emotion.fromLevel(record.emotionLevel)
                if (isToday) todayRecorded = true
            }

            if (isToday) todayStampIndex = stamps.size

            stamps.add(
                DayStamp(
                    dayIndex = dayIndex,
                    label    = date.dayLabel(),
                    state    = state,
                    emotion  = emotion,
                    isToday  = isToday,
                )
            )
        }

        return SectionData(
            weekNumber      = weekNumber,
            sectionStart    = sectionStart,
            sectionEnd      = sectionEnd,
            stamps          = stamps,
            todayRecorded   = todayRecorded,
            todayStampIndex = todayStampIndex,
        )
    }

    private fun LocalDate.dayLabel(): String = dayOfWeek.label()
    private fun DayOfWeek.label() = when (this) {
        DayOfWeek.MONDAY    -> "월"; DayOfWeek.TUESDAY -> "화"; DayOfWeek.WEDNESDAY -> "수"
        DayOfWeek.THURSDAY  -> "목"; DayOfWeek.FRIDAY  -> "금"; DayOfWeek.SATURDAY  -> "토"
        DayOfWeek.SUNDAY    -> "일"
    }
    // dayIndex 0=월 ~ 6=일 (섹션 내 인덱스가 아닌 요일 순서 — 섹션 시작일에 따라 다를 수 있음)
    private fun DayOfWeek.index(): Int = value - 1  // ISO: MONDAY=1 → 0
}
