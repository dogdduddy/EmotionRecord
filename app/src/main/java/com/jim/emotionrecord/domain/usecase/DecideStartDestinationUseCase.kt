package com.jim.emotionrecord.domain.usecase

import com.jim.emotionrecord.data.repository.EmotionRepository
import javax.inject.Inject

enum class StartDestination { RECORD, HOME }

class DecideStartDestinationUseCase @Inject constructor(
    private val repository: EmotionRepository
) {
    suspend operator fun invoke(): StartDestination {
        val latest = repository.getLatest() ?: return StartDestination.RECORD
        val now = System.currentTimeMillis()
        return if (now - latest.recordedAt > 30 * 60 * 1000L) {
            StartDestination.RECORD
        } else {
            StartDestination.HOME
        }
    }
}
