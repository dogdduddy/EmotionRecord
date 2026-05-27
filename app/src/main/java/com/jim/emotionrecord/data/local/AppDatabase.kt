package com.jim.emotionrecord.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jim.emotionrecord.data.local.dao.EmotionCheckDao
import com.jim.emotionrecord.data.local.dao.UserMetaDao
import com.jim.emotionrecord.data.local.entity.EmotionCheckEntity
import com.jim.emotionrecord.data.local.entity.UserMetaEntity

@Database(
    entities = [EmotionCheckEntity::class, UserMetaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun emotionCheckDao(): EmotionCheckDao
    abstract fun userMetaDao(): UserMetaDao
}
