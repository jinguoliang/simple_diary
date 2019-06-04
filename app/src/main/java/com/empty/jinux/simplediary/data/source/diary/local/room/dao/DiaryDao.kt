package com.empty.jinux.simplediary.data.source.diary.local.room.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.empty.jinux.simplediary.data.source.diary.local.room.COLUMN_ID
import com.empty.jinux.simplediary.data.source.diary.local.room.TABLE_DIARY
import com.empty.jinux.simplediary.data.source.diary.local.room.entity.Diary

/**
 * Created by jingu on 2018/3/2.
 */

@Dao
interface DiaryDao {
    @Insert(onConflict = REPLACE)
    fun insertOne(diary: Diary): Long

    @Query("SELECT * FROM $TABLE_DIARY")
    fun getAll(): List<Diary>

    @Query("SELECT * FROM $TABLE_DIARY WHERE $COLUMN_ID LIKE :id LIMIT 1")
    fun getOneById(id: Long): Diary?

    @Delete
    fun delete(diary: Diary)

    @Query("DELETE FROM $TABLE_DIARY WHERE $COLUMN_ID LIKE :id")
    fun deleteById(id: Long)

    @Update()
    fun updateState(diary: Diary)
}
