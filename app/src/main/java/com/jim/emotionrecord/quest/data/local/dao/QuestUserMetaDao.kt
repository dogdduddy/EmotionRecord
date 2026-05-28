package com.jim.emotionrecord.quest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jim.emotionrecord.quest.data.local.entity.QuestUserMetaEntity

@Dao
interface QuestUserMetaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: QuestUserMetaEntity)

    @Query("SELECT * FROM quest_user_meta WHERE id = 1")
    suspend fun get(): QuestUserMetaEntity?

    @Query("DELETE FROM quest_user_meta")
    suspend fun deleteAll()
}
