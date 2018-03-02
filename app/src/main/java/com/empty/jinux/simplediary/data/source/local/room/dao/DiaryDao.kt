package com.empty.jinux.simplediary.data.source.local.room.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.empty.jinux.simplediary.data.source.local.room.COLUMN_ID
import com.empty.jinux.simplediary.data.source.local.room.TABLE_DIARY
import com.empty.jinux.simplediary.data.source.local.room.entity.Diary

/**
 * Created by jingu on 2018/3/2.
 */

@Dao
interface DiaryDao {
    @Insert(onConflict = REPLACE)
    fun insertOne(diary: Diary)

    @Query("SELECT * FROM $TABLE_DIARY")
    fun getAll(): List<Diary>

    @Query("SELECT * FROM $TABLE_DIARY WHERE $COLUMN_ID LIKE :id LIMIT 1")
    fun getOneById(id: Int): Diary?

    @Delete
    fun delete(diary: Diary)

    @Query("DELETE FROM $TABLE_DIARY WHERE $COLUMN_ID LIKE :id")
    fun deleteById(id: Int)

    @Query("DELETE FROM $TABLE_DIARY")
    fun deleteAll()
}