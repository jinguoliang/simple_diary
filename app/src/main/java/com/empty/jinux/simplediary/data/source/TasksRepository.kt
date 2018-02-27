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
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 *
 *
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local mDatabase doesn't
 * exist or is empty.
 *
 *
 * By marking the constructor with `@Inject` and the class with `@Singleton`, Dagger
 * injects the dependencies required to create an instance of the TasksRespository (if it fails, it
 * emits a compiler error). It uses [TasksRepositoryModule] to do so, and the constructed
 * instance is available in [TasksRepositoryComponent].
 *
 *
 * Dagger generated code doesn't require public access to the constructor or class, and
 * therefore, to ensure the developer doesn't instantiate the class manually and bypasses Dagger,
 * it's good practice minimise the visibility of the class/constructor as much as possible.
 */
@Singleton
class TasksRepository
/**
 * By marking the constructor with `@Inject`, Dagger will try to inject the dependencies
 * required to create an instance of the TasksRepository. Because [TasksDataSource] is an
 * interface, we must provide to Dagger a way to build those arguments, this is done in
 * [TasksRepositoryModule].
 * <P>
 * When two arguments or more have the same type, we must provide to Dagger a way to
 * differentiate them. This is done using a qualifier.
</P> *
 *
 * Dagger strictly enforces that arguments not marked with `@Nullable` are not injected
 * with `@Nullable` values.
 */
@Inject
internal constructor(@param:Remote private val mRemoteDataSource: TasksDataSource,
                     @param:Local private val mLocalDataSource: TasksDataSource) : TasksDataSource {

    /**
     * Gets tasks from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     *
     *
     * Note: [LoadTasksCallback.onDataNotAvailable] is fired if all data sources fail to
     * get the data.
     */
    override fun getDiaries(callback: TasksDataSource.LoadDiariesCallback) {
        // Query the local storage if available. If not, query the network.
        mLocalDataSource.getDiaries(object : TasksDataSource.LoadDiariesCallback {
            override fun onTasksLoaded(tasks: List<Diary>) {
                callback.onTasksLoaded(tasks)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })

        mRemoteDataSource.getDiaries(object : TasksDataSource.LoadDiariesCallback {
            override fun onTasksLoaded(tasks: List<Diary>) {
                callback.onTasksLoaded(tasks)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    override fun save(task: Diary) {
        mRemoteDataSource.save(task)
        mLocalDataSource.save(task)
    }

    override fun clearCompletedTasks() {
        mRemoteDataSource.clearCompletedTasks()
        mLocalDataSource.clearCompletedTasks()
    }

    /**
     * Gets tasks from local data source (sqlite) unless the table is new or empty. In that case it
     * uses the network data source. This is done to simplify the sample.
     */
    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallback) {
        // Is the task in the local data source? If not, query the network.
        mLocalDataSource.getTask(taskId, object : TasksDataSource.GetTaskCallback {
            override fun onTaskLoaded(task: Diary?) {
                callback.onTaskLoaded(task)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    override fun refreshTasks() {
    }

    override fun deleteAllDiaries() {
        mRemoteDataSource.deleteAllDiaries()
        mLocalDataSource.deleteAllDiaries()
    }

    override fun deleteTask(taskId: String) {
        mRemoteDataSource.deleteTask(taskId)
        mLocalDataSource.deleteTask(taskId)
    }

    private fun getTasksFromRemoteDataSource(callback: TasksDataSource.LoadDiariesCallback) {
        mRemoteDataSource.getDiaries(object : TasksDataSource.LoadDiariesCallback {
            override fun onTasksLoaded(tasks: List<Diary>) {
                refreshLocalDataSource(tasks)
                callback.onTasksLoaded(tasks)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    private fun refreshLocalDataSource(diaries: List<Diary>) {
        mLocalDataSource.deleteAllDiaries()
        for (diary in diaries) {
            mLocalDataSource.save(diary)
        }
    }
}
