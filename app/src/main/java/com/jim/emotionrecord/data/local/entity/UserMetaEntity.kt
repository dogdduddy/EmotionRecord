package com.jim.emotionrecord.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_meta")
data class UserMetaEntity(
    @PrimaryKey val id: Int = 1,  // single row
    val firstRecordedAt: Long? = null
)
