package com.jim.emotionrecord.domain.usecase

import com.jim.emotionrecord.data.repository.EmotionRepository
import com.jim.emotionrecord.domain.model.DaySummary
import com.jim.emotionrecord.domain.model.Emotion
import com.jim.emotionrecord.domain.model.EmotionCheck
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

class GetDaySummariesInWeekUseCase @Inject constructor(
    private val repository: EmotionRepository
) {
    suspend operator fun invoke(weekStartMillis: Long): List<DaySummary> {
        val weekEndMillis = weekStartMillis + 7L * 24 * 60 * 60 * 1000 - 1
        val entities = repository.getInRange(weekStartMillis, weekEndMillis)
        val zone = ZoneId.systemDefault()

        // 최신 기록 id (isMemoEditable 결정용)
        val latestId = repository.getLatest()?.id

        // 7일 각각에 대해 DaySummary 생성
        return (0 until 7).map { dayOffset ->
            val dayStart = weekStartMillis + dayOffset * 24 * 60 * 60 * 1000L
            val dayEnd = dayStart + 24 * 60 * 60 * 1000L - 1
            val dayEntities = entities.filter { it.recordedAt in dayStart..dayEnd }
                .sortedBy { it.recordedAt }

            val checks = dayEntities.map { entity ->
                EmotionCheck(
                    id = entity.id,
                    emotion = Emotion.fromScore(entity.emotionScore),
                    recordedAt = Instant.ofEpochMilli(entity.recordedAt),
                    memo = entity.memo,
                    isMemoEditable = entity.id == latestId
                )
            }

            DaySummary(
                date = Instant.ofEpochMilli(dayStart).atZone(zone).toLocalDate(),
                averageScore = if (checks.isEmpty()) null
                               else checks.map { it.emotion.score }.average().toFloat(),
                records = checks
            )
        }
    }
}
