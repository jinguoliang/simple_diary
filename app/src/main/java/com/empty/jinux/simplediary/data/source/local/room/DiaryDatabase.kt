package com.empty.jinux.simplediary.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.empty.jinux.simplediary.data.source.local.room.dao.DiaryDao
import com.empty.jinux.simplediary.data.source.local.room.entity.Diary

/**
 * Created by jingu on 2018/3/2.
 *
 *
 */

@Database(entities = arrayOf(Diary::class), version = 1, exportSchema = false)
abstract class DiaryDatabase : RoomDatabase() {
    abstract fun diaryDao(): DiaryDao
}