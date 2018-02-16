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

package com.empty.jinux.simplediary.data.source.remote

import com.empty.jinux.baselibaray.logThrowable
import com.empty.jinux.baselibaray.logd
import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.data.source.TasksDataSource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import javax.inject.Singleton

/**
 * Implementation of the data source that adds a latency simulating network.
 */

inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object : TypeToken<T>() {}.type)

@Singleton
class DiariesRemoteDataSource : TasksDataSource {
    private val diaries_root = "diary"

    val mDataMap: MutableMap<String, Diary> = mutableMapOf()
    val mDataList: MutableList<Diary> = mutableListOf()

    val mDatabase = FirebaseDatabase.getInstance().getReference(diaries_root).apply {
        addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                logThrowable(p0.toException(), "FirebaseDatabase")
            }

            override fun onDataChange(ds: DataSnapshot) {
                mDataList.clear()
                mDataMap.clear()

                ds.value.apply {
                    when (this) {
                        is HashMap<*, *> -> {
                            val iter = this.iterator()
                            for (item in iter) {
                                addItem(item.value as String)
                            }
                        }
                        is String -> {
                            addItem(this)
                        }
                    }
                }
            }

        })
    }

    private fun addItem(json: String) {
        json.apply {
            logd("json = $json")
            val data: Diary = Gson().fromJson(json, Diary::class.java)
            mDataMap[data.id] = data
            mDataList.add(data)
        }
    }

    /**
     * Note: [LoadTasksCallback.onDataNotAvailable] is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    override fun getDiaries(callback: TasksDataSource.LoadDiariesCallback) {
        callback.onTasksLoaded(mDataList)
    }

    /**
     * Note: [GetTaskCallback.onDataNotAvailable] is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallback) {
        if (mDataMap.containsKey(taskId)) {
            callback.onTaskLoaded(mDataMap[taskId]!!)
        } else {
            callback.onDataNotAvailable()
        }
    }


    override fun saveTask(task: Diary) {
        mDatabase.child(task.id).setValue(Gson().toJson(task))
    }

    override fun completeTask(task: Diary) {
        val completedTask = Diary(task.title, task.description, task.id, true)
        mDatabase.child(task.id).setValue(Gson().toJson(completedTask))
    }

    override fun completeTask(taskId: String) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun activateTask(task: Diary) {
        val activeTask = Diary(task.title, task.description, task.id)
        mDatabase.child(task.id).setValue(Gson().toJson(activeTask))
    }

    override fun activateTask(taskId: String) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun clearCompletedTasks() {
        val it = mDataList.iterator()

        while (it.hasNext()) {
            val entry = it.next()
            if (entry.isCompleted) {
                mDatabase.child(entry.id).removeValue()
            }
        }
    }

    override fun refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    override fun deleteAllTasks() {
        mDatabase.removeValue()
    }

    override fun deleteTask(taskId: String) {
        mDatabase.child(taskId).removeValue()
    }

    companion object {

        private val SERVICE_LATENCY_IN_MILLIS = 5000

        init {
//            addTask("Build tower in Pisa", "Ground looks good, no foundation work required.")
//            addTask("Finish bridge in Tacoma", "Found awesome girders at half the cost!")
        }
    }
}
