package com.empty.jinux.simplediary.data.source.local.room.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.empty.jinux.simplediary.data.source.local.room.COLUMN_ID
import com.empty.jinux.simplediary.data.source.local.room.TABLE_DIARY
import com.empty.jinux.simplediary.data.source.local.room.entity.Diary

/**
 * Created by jingu on 2018/3/2.
 */

@Dao
interface DiaryDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertOne(diary: Diary): Long

    @Query("SELECT * FROM $TABLE_DIARY")
    suspend fun getAll(): List<Diary>

    @Query("SELECT * FROM $TABLE_DIARY WHERE $COLUMN_ID LIKE :id LIMIT 1")
    suspend fun getOneById(id: Long): Diary?

    @Delete
    suspend fun delete(diary: Diary)

    @Query("DELETE FROM $TABLE_DIARY WHERE $COLUMN_ID LIKE :id")
    suspend fun deleteById(id: Long)

    @Update()
    suspend fun updateState(diary: Diary)
}