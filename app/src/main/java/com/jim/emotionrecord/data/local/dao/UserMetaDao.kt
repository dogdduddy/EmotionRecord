package com.jim.emotionrecord.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jim.emotionrecord.data.local.entity.UserMetaEntity

@Dao
interface UserMetaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: UserMetaEntity)

    @Query("SELECT * FROM user_meta WHERE id = 1")
    suspend fun get(): UserMetaEntity?

    @Query("DELETE FROM user_meta")
    suspend fun deleteAll()
}
