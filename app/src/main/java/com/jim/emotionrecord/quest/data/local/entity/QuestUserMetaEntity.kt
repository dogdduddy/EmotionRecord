package com.jim.emotionrecord.quest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quest_user_meta")
data class QuestUserMetaEntity(
    @PrimaryKey val id: Int = 1,
    val firstRecordedAt: Long? = null,
    val onboardingSeen: Boolean = false,
)
