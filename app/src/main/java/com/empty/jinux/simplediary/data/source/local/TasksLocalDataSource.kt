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
import com.empty.jinux.simplediary.data.source.TasksDataSource
import com.google.common.base.Preconditions.checkNotNull
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Concrete implementation of a data source as a db.
 */
@Singleton
class TasksLocalDataSource @Inject
constructor(context: Context) : TasksDataSource {

    private val mDbHelper: TasksDbHelper

    init {
        checkNotNull(context)
        mDbHelper = TasksDbHelper(context)
    }

    /**
     * Note: [LoadTasksCallback.onDataNotAvailable] is fired if the mDatabase doesn't exist
     * or the table is empty.
     */
    override fun getDiaries(callback: TasksDataSource.LoadDiariesCallback) {
        val tasks = ArrayList<Diary>()
        val db = mDbHelper.readableDatabase

        val projection = arrayOf<String>(TaskEntry.COLUMN_NAME_ENTRY_ID, TaskEntry.COLUMN_NAME_TITLE, TaskEntry.COLUMN_NAME_DESCRIPTION, TaskEntry.COLUMN_NAME_COMPLETED)

        val c = db.query(
                TaskEntry.TABLE_NAME, projection, null, null, null, null, null)

        if (c != null && c.count > 0) {
            while (c.moveToNext()) {
                val itemId = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_ENTRY_ID))
                val title = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TITLE))
                val description = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DESCRIPTION))
                val completed = c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_COMPLETED)) == 1
                val task = Diary(title, description, itemId, completed)
                tasks.add(task)
            }
        }
        c?.close()

        db.close()

        callback.onTasksLoaded(tasks)

    }

    /**
     * Note: [GetTaskCallback.onDataNotAvailable] is fired if the [Diary] isn't
     * found.
     */
    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallback) {
        val db = mDbHelper.readableDatabase

        val projection = arrayOf<String>(TaskEntry.COLUMN_NAME_ENTRY_ID, TaskEntry.COLUMN_NAME_TITLE, TaskEntry.COLUMN_NAME_DESCRIPTION, TaskEntry.COLUMN_NAME_COMPLETED)

        val selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?"
        val selectionArgs = arrayOf(taskId)

        val c = db.query(
                TaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null)

        var task: Diary? = null

        if (c != null && c.count > 0) {
            c.moveToFirst()
            val itemId = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_ENTRY_ID))
            val title = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TITLE))
            val description = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DESCRIPTION))
            val completed = c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_COMPLETED)) == 1
            task = Diary(title, description, itemId, completed)
        }
        c?.close()

        db.close()

        if (task != null) {
            callback.onTaskLoaded(task)
        } else {
            callback.onDataNotAvailable()
        }
    }

    override fun save(newDiary: Diary) {
        getTask(newDiary.id, object : TasksDataSource.GetTaskCallback {
            override fun onTaskLoaded(diary: Diary) {
                updateDiary(newDiary)
            }
            override fun onDataNotAvailable() {
                insertDiary(newDiary)
            }
        })
    }

    private fun updateDiary(diary: Diary) {
        val db = mDbHelper.writableDatabase

        val values = ContentValues()
        values.put(TaskEntry.COLUMN_NAME_TITLE, diary.title)
        values.put(TaskEntry.COLUMN_NAME_DESCRIPTION, diary.description)
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, diary.isCompleted)

        db.update(TaskEntry.TABLE_NAME, values,
                "${TaskEntry.COLUMN_NAME_ENTRY_ID} = ?", arrayOf(diary.id))

        db.close()
    }

    private fun insertDiary(diary: Diary) {
        val db = mDbHelper.writableDatabase

        val values = ContentValues()
        values.put(TaskEntry.COLUMN_NAME_ENTRY_ID, diary.id)
        values.put(TaskEntry.COLUMN_NAME_TITLE, diary.title)
        values.put(TaskEntry.COLUMN_NAME_DESCRIPTION, diary.description)
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, diary.isCompleted)

        db.insert(TaskEntry.TABLE_NAME, null, values)

        db.close()
    }

    override fun clearCompletedTasks() {
        val db = mDbHelper.writableDatabase

        val selection = TaskEntry.COLUMN_NAME_COMPLETED + " LIKE ?"
        val selectionArgs = arrayOf("1")

        db.delete(TaskEntry.TABLE_NAME, selection, selectionArgs)

        db.close()
    }

    override fun refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    override fun deleteAllDiaries() {
        val db = mDbHelper.writableDatabase

        db.delete(TaskEntry.TABLE_NAME, null, null)

        db.close()
    }

    override fun deleteTask(taskId: String) {
        val db = mDbHelper.writableDatabase

        val selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?"
        val selectionArgs = arrayOf(taskId)

        db.delete(TaskEntry.TABLE_NAME, selection, selectionArgs)

        db.close()
    }
}
