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
class GetQuestMapSectionsUseCase @Inject constructor(
    private val emotionRecordRepository: EmotionRecordRepository,
    private val questUserMetaRepository: QuestUserMetaRepository,
) {
    suspend operator fun invoke(): List<SectionData> {
        val meta = questUserMetaRepository.get()
        val firstMs = meta?.firstRecordedAt

        val today = LocalDate.now()
        val zone = ZoneId.systemDefault()

        val firstDate = if (firstMs == null) {
            // 기록이 없으면 오늘을 월요일 기준으로 잡거나, 오늘부터 시작하는 섹션
            // 여기선 오늘을 포함하는 월~일 섹션으로 시작
            today.with(DayOfWeek.MONDAY)
        } else {
            Instant.ofEpochMilli(firstMs).atZone(zone).toLocalDate()
        }

        val daysSinceFirst = ChronoUnit.DAYS.between(firstDate, today).toInt().coerceAtLeast(0)
        val currentSectionIndex = daysSinceFirst / 7
        
        val sections = mutableListOf<SectionData>()
        
        for (idx in 0..currentSectionIndex) {
            val sectionStart = firstDate.plusDays((idx * 7).toLong())
            val sectionEnd = sectionStart.plusDays(6)
            
            val stamps = mutableListOf<DayStamp>()
            var todayRecorded = false
            var todayStampIndex: Int? = null

            for (dayOffset in 0..6) {
                val date = sectionStart.plusDays(dayOffset.toLong())
                val dayStart = date.atStartOfDay(zone).toInstant().toEpochMilli()
                val dayEnd   = date.atTime(23, 59, 59).atZone(zone).toInstant().toEpochMilli()
                val records  = emotionRecordRepository.getInRange(dayStart, dayEnd)
                val isToday  = date == today

                val state: StampState
                val emotion: Emotion?

                if (records.isEmpty()) {
                    state = when {
                        isToday -> StampState.TODAY
                        date.isAfter(today) -> StampState.FUTURE
                        else -> StampState.SKIPPED
                    }
                    emotion = null
                } else {
                    val record = records.last()
                    state = if (record.missionCompleted) StampState.MISSION else StampState.COMPLETE
                    emotion = Emotion.fromLevel(record.emotionLevel)
                    if (isToday) todayRecorded = true
                }

                if (isToday) todayStampIndex = stamps.size

                stamps.add(
                    DayStamp(
                        dayIndex = dayOffset, // 0~6
                        label    = date.dayLabel(),
                        state    = state,
                        emotion  = emotion,
                        isToday  = isToday,
                    )
                )
            }

            sections.add(
                SectionData(
                    weekNumber      = idx + 1,
                    sectionStart    = sectionStart,
                    sectionEnd      = sectionEnd,
                    stamps          = stamps,
                    todayRecorded   = todayRecorded,
                    todayStampIndex = todayStampIndex,
                )
            )
        }

        return sections
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
