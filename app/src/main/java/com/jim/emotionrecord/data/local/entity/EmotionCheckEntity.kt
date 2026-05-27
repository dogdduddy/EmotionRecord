package com.jim.emotionrecord.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emotion_check")
data class EmotionCheckEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val emotionScore: Int,        // 1~5 (1: 매우 나쁨 ~ 5: 매우 좋음)
    val recordedAt: Long,         // epochMilli
    val memo: String = ""
)
