package com.jim.emotionrecord.quest.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jim.emotionrecord.quest.data.local.dao.EmotionRecordDao
import com.jim.emotionrecord.quest.data.local.dao.QuestUserMetaDao
import com.jim.emotionrecord.quest.data.local.entity.EmotionRecordEntity
import com.jim.emotionrecord.quest.data.local.entity.QuestUserMetaEntity

@Database(
    entities = [EmotionRecordEntity::class, QuestUserMetaEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class QuestDatabase : RoomDatabase() {
    abstract fun emotionRecordDao(): EmotionRecordDao
    abstract fun questUserMetaDao(): QuestUserMetaDao
}
