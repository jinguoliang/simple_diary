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

package com.empty.jinux.simplediary.data.source

import com.empty.jinux.simplediary.data.Diary

/**
 * Main entry point for accessing tasks data.
 *
 *
 * For simplicity, only getDiaries() and getTask() have callbacks. Consider adding callbacks to other
 * methods to inform the user of network/mDatabase errors or successful operations.
 * For example, when a new task is created, it's synchronously stored in cache but usually every
 * operation on mDatabase or network should be executed in a different thread.
 */
interface TasksDataSource {

    interface LoadDiariesCallback {

        fun onTasksLoaded(tasks: List<Diary>)

        fun onDataNotAvailable()
    }

    interface GetTaskCallback {

        fun onTaskLoaded(diary: Diary?)

        fun onDataNotAvailable()
    }

    interface OnChangeListener {
        fun onChange(data: List<Diary>)
    }

    fun getDiaries(callback: LoadDiariesCallback)

    fun getTask(taskId: String, callback: GetTaskCallback)

    fun save(task: Diary)

    fun clearCompletedTasks()

    fun refreshTasks()

    fun deleteAllDiaries()

    fun deleteTask(taskId: String)

}
