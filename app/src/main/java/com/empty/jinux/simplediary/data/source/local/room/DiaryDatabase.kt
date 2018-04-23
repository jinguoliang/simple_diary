package com.empty.jinux.simplediary.data.source.local.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
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