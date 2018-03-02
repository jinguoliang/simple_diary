/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.empty.jinux.simplediary.data.source.local

import android.content.ContentValues
import android.content.Context
import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.data.source.DiariesDataSource
import com.google.common.base.Preconditions.checkNotNull
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Concrete implementation of a data source as a db.
 */
@Singleton
class DiariesLocalDataSource @Inject
constructor(context: Context) : DiariesDataSource {

    private val mDbHelper: DiaryDbHelper

    init {
        checkNotNull(context)
        mDbHelper = DiaryDbHelper(context)
    }

    override fun getDiaries(callback: DiariesDataSource.LoadDiariesCallback) {
        val diaries = ArrayList<Diary>()
        val db = mDbHelper.readableDatabase

        val projection = arrayOf(DiaryEntry.COLUMN_NAME_ENTRY_ID, DiaryEntry.COLUMN_NAME_TITLE, DiaryEntry.COLUMN_NAME_DESCRIPTION, DiaryEntry.COLUMN_NAME_COMPLETED)

        val c = db.query(
                DiaryEntry.TABLE_NAME, projection, null, null, null, null, null)

        if (c != null && c.count > 0) {
            while (c.moveToNext()) {
                val itemId = c.getString(c.getColumnIndexOrThrow(DiaryEntry.COLUMN_NAME_ENTRY_ID))
                val title = c.getString(c.getColumnIndexOrThrow(DiaryEntry.COLUMN_NAME_TITLE))
                val description = c.getString(c.getColumnIndexOrThrow(DiaryEntry.COLUMN_NAME_DESCRIPTION))
                val completed = c.getInt(c.getColumnIndexOrThrow(DiaryEntry.COLUMN_NAME_COMPLETED)) == 1
                val diary = Diary(title, description, itemId, completed)
                diaries.add(diary)
            }
        }
        c?.close()

        db.close()

        callback.onDiariesLoaded(diaries)

    }

    override fun getDiary(diaryId: String, callback: DiariesDataSource.GetDiaryCallback) {
        val db = mDbHelper.readableDatabase

        val projection = arrayOf(DiaryEntry.COLUMN_NAME_ENTRY_ID, DiaryEntry.COLUMN_NAME_TITLE, DiaryEntry.COLUMN_NAME_DESCRIPTION, DiaryEntry.COLUMN_NAME_COMPLETED)

        val selection = DiaryEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?"
        val selectionArgs = arrayOf(diaryId)

        val c = db.query(
                DiaryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null)

        var diary: Diary? = null

        if (c != null && c.count > 0) {
            c.moveToFirst()
            val itemId = c.getString(c.getColumnIndexOrThrow(DiaryEntry.COLUMN_NAME_ENTRY_ID))
            val title = c.getString(c.getColumnIndexOrThrow(DiaryEntry.COLUMN_NAME_TITLE))
            val description = c.getString(c.getColumnIndexOrThrow(DiaryEntry.COLUMN_NAME_DESCRIPTION))
            val completed = c.getInt(c.getColumnIndexOrThrow(DiaryEntry.COLUMN_NAME_COMPLETED)) == 1
            diary = Diary(title, description, itemId, completed)
        }
        c?.close()

        db.close()

        if (diary != null) {
            callback.onDiaryLoaded(diary)
        } else {
            callback.onDataNotAvailable()
        }
    }

    override fun save(diary: Diary) {
        getDiary(diary.id, object : DiariesDataSource.GetDiaryCallback {
            override fun onDiaryLoaded(diary: Diary) {
                updateDiary(diary)
            }
            override fun onDataNotAvailable() {
                insertDiary(diary)
            }
        })
    }

    private fun updateDiary(diary: Diary) {
        val db = mDbHelper.writableDatabase

        val values = ContentValues()
        values.put(DiaryEntry.COLUMN_NAME_TITLE, diary.title)
        values.put(DiaryEntry.COLUMN_NAME_DESCRIPTION, diary.description)
        values.put(DiaryEntry.COLUMN_NAME_COMPLETED, diary.isCompleted)

        db.update(DiaryEntry.TABLE_NAME, values,
                "${DiaryEntry.COLUMN_NAME_ENTRY_ID} = ?", arrayOf(diary.id))

        db.close()
    }

    private fun insertDiary(diary: Diary) {
        val db = mDbHelper.writableDatabase

        val values = ContentValues()
        values.put(DiaryEntry.COLUMN_NAME_ENTRY_ID, diary.id)
        values.put(DiaryEntry.COLUMN_NAME_TITLE, diary.title)
        values.put(DiaryEntry.COLUMN_NAME_DESCRIPTION, diary.description)
        values.put(DiaryEntry.COLUMN_NAME_COMPLETED, diary.isCompleted)

        db.insert(DiaryEntry.TABLE_NAME, null, values)

        db.close()
    }

    override fun refreshDiaries() {
    }

    override fun deleteAllDiaries() {
        val db = mDbHelper.writableDatabase

        db.delete(DiaryEntry.TABLE_NAME, null, null)

        db.close()
    }

    override fun deleteDiary(diaryId: String) {
        val db = mDbHelper.writableDatabase

        val selection = DiaryEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?"
        val selectionArgs = arrayOf(diaryId)

        db.delete(DiaryEntry.TABLE_NAME, selection, selectionArgs)

        db.close()
    }
}
