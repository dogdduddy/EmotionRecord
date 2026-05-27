package com.jim.emotionrecord.domain.usecase

import com.jim.emotionrecord.data.repository.EmotionRepository
import javax.inject.Inject

class UpdateMemoUseCase @Inject constructor(
    private val repository: EmotionRepository
) {
    suspend operator fun invoke(id: Long, memo: String) {
        val latest = repository.getLatest() ?: return
        if (latest.id == id) {
            repository.update(latest.copy(memo = memo))
        }
    }
}
