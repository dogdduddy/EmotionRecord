package com.jim.emotionrecord.domain.usecase

import com.jim.emotionrecord.data.repository.EmotionRepository
import com.jim.emotionrecord.data.repository.UserMetaRepository
import com.jim.emotionrecord.domain.model.WeekSummary
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

class GetWeekSummariesUseCase @Inject constructor(
    private val repository: EmotionRepository,
    private val userMetaRepository: UserMetaRepository
) {
    suspend operator fun invoke(): List<WeekSummary> {
        val firstRecordedAt = userMetaRepository.getFirstRecordedAt() ?: return emptyList()
        val now = System.currentTimeMillis()
        val records = repository.getAll()

        val weekMillis = 7L * 24 * 60 * 60 * 1000
        val totalWeeks = ((now - firstRecordedAt) / weekMillis).toInt() + 1

        return (0 until totalWeeks).map { weekIdx ->
            val weekStart = firstRecordedAt + weekIdx * weekMillis
            val weekEnd = weekStart + weekMillis - 1
            val weekRecords = records.filter { it.recordedAt in weekStart..weekEnd }
            val zone = ZoneId.systemDefault()

            WeekSummary(
                weekIndex = weekIdx + 1,
                startDate = Instant.ofEpochMilli(weekStart).atZone(zone).toLocalDate(),
                endDate = Instant.ofEpochMilli(weekEnd).atZone(zone).toLocalDate(),
                averageScore = if (weekRecords.isEmpty()) null
                               else weekRecords.map { it.emotionScore }.average().toFloat(),
                recordCount = weekRecords.size
            )
        }
    }
}
