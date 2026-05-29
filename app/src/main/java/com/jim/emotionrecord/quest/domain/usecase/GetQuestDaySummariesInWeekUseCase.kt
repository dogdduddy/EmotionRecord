package com.jim.emotionrecord.quest.domain.usecase

import com.jim.emotionrecord.domain.model.DaySummary
import com.jim.emotionrecord.domain.model.Emotion
import com.jim.emotionrecord.domain.model.EmotionCheck
import com.jim.emotionrecord.quest.data.repository.EmotionRecordRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetQuestDaySummariesInWeekUseCase @Inject constructor(
    private val repository: EmotionRecordRepository
) {
    suspend operator fun invoke(weekStartMillis: Long): List<DaySummary> {
        val zone = ZoneId.systemDefault()
        val weekStart = Instant.ofEpochMilli(weekStartMillis).atZone(zone).toLocalDate()
        
        val result = mutableListOf<DaySummary>()
        for (i in 0..6) {
            val date = weekStart.plusDays(i.toLong())
            val startMs = date.atStartOfDay(zone).toInstant().toEpochMilli()
            val endMs = date.atTime(23, 59, 59).atZone(zone).toInstant().toEpochMilli()
            
            val records = repository.getInRange(startMs, endMs)
            
            result.add(
                DaySummary(
                    date = date,
                    averageScore = if (records.isEmpty()) null
                                   else records.map { it.emotionLevel }.average().toFloat(),
                    records = records.map { entity ->
                        EmotionCheck(
                            id = entity.id,
                            emotion = Emotion.fromScore(entity.emotionLevel),
                            memo = entity.memo,
                            recordedAt = Instant.ofEpochMilli(entity.recordedAt)
                        )
                    }
                )
            )
        }
        return result
    }
}
