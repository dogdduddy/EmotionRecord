package com.jim.emotionrecord.quest.domain.usecase

import com.jim.emotionrecord.domain.model.WeekSummary
import com.jim.emotionrecord.quest.data.repository.EmotionRecordRepository
import com.jim.emotionrecord.quest.data.repository.QuestUserMetaRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetQuestWeekSummariesUseCase @Inject constructor(
    private val repository: EmotionRecordRepository,
    private val questUserMetaRepository: QuestUserMetaRepository
) {
    suspend operator fun invoke(): List<WeekSummary> {
        val meta = questUserMetaRepository.get() ?: return emptyList()
        val firstRecordedAt = meta.firstRecordedAt ?: return emptyList()
        val zone = ZoneId.systemDefault()
        val firstDate = Instant.ofEpochMilli(firstRecordedAt).atZone(zone).toLocalDate()
        val today = LocalDate.now()
        
        // 월요일 시작 기준으로 주차 계산
        val startOfFirstWeek = firstDate.with(java.time.DayOfWeek.MONDAY)
        val daysSinceStart = ChronoUnit.DAYS.between(startOfFirstWeek, today).coerceAtLeast(0)
        val totalWeeks = (daysSinceStart / 7).toInt() + 1

        val result = mutableListOf<WeekSummary>()
        for (idx in 0 until totalWeeks) {
            val weekStart = startOfFirstWeek.plusWeeks(idx.toLong())
            val weekEnd = weekStart.plusDays(6)
            
            val startMs = weekStart.atStartOfDay(zone).toInstant().toEpochMilli()
            val endMs = weekEnd.atTime(23, 59, 59).atZone(zone).toInstant().toEpochMilli()
            
            val records = repository.getInRange(startMs, endMs)
            
            result.add(
                WeekSummary(
                    weekIndex = idx + 1,
                    startDate = weekStart,
                    endDate = weekEnd,
                    averageScore = if (records.isEmpty()) null
                                   else records.map { it.emotionLevel }.average().toFloat(),
                    recordCount = records.size
                )
            )
        }
        return result
    }
}
