package com.jim.emotionrecord.domain.model

import java.time.Instant

data class EmotionCheck(
    val id: Long,
    val emotion: Emotion,
    val recordedAt: Instant,
    val memo: String,
    val isMemoEditable: Boolean = false  // 최신 1건만 true
)
