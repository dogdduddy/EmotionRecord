package com.jim.emotionrecord.domain.usecase

import com.jim.emotionrecord.data.local.entity.EmotionCheckEntity
import com.jim.emotionrecord.data.repository.EmotionRepository
import com.jim.emotionrecord.data.repository.UserMetaRepository
import javax.inject.Inject

class RecordEmotionUseCase @Inject constructor(
    private val emotionRepository: EmotionRepository,
    private val userMetaRepository: UserMetaRepository
) {
    suspend operator fun invoke(score: Int, memo: String = "") {
        val now = System.currentTimeMillis()
        emotionRepository.insert(
            EmotionCheckEntity(
                emotionScore = score,
                recordedAt = now,
                memo = memo
            )
        )
        // 첫 기록이면 UserMeta에 저장
        if (userMetaRepository.getFirstRecordedAt() == null) {
            userMetaRepository.setFirstRecordedAt(now)
        }
    }
}
