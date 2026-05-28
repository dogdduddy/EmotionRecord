package com.jim.emotionrecord.quest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emotion_record")
data class EmotionRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val emotionLevel: Int,           // 1~5
    val memo: String = "",
    val missionCompleted: Boolean = false,  // 미션까지 완료했는지 → 스탬프 MISSION 상태
    val recordedAt: Long,            // epochMilli
)
