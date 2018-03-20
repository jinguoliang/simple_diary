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
import com.empty.jinux.simplediary.data.source.DiariesDataSource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import java.util.*
import javax.inject.Singleton

/**
 * Implementation of the data source with Firebase
 */
@Singleton
class DiariesRemoteDataSource : DiariesDataSource {


    private var mEventListener: DatabaseDataChangeListener? = null

    private val mDataList = mutableListOf<Diary>()
    private val mDataMap = mutableMapOf<Long, Diary>()

    private var mCacheDirty = true

    private val mDatabase = FirebaseDatabase.getInstance().getReference(diaries_root).apply {
        mEventListener = DatabaseDataChangeListener {
            mDataList.clear()
            mDataList.addAll(it)
            mDataMap.clear()
            it.forEach {
                mDataMap.put(it.id, it)
            }
            mCacheDirty = true

        }
        this.addValueEventListener(mEventListener)
    }

    override fun getDiaries(callback: DiariesDataSource.LoadDiariesCallback) {
        if (mCacheDirty) {

        } else {
            callback.onDiariesLoaded(mDataList)
        }
    }

    override fun getDiary(diaryId: Long, callback: DiariesDataSource.GetDiaryCallback) {
        if (mCacheDirty) {

        } else {
            if (mDataMap.containsKey(diaryId)) {
                callback.onDiaryLoaded(mDataMap[diaryId]!!)
            } else {
                callback.onDataNotAvailable()
            }
        }
    }


    override fun save(diary: Diary, callback: DiariesDataSource.OnCallback<Long>) {
        mDatabase.child(diary.id.toString()).setValue(Gson().toJson(diary))
        mCacheDirty = true
    }

    override fun refreshDiaries() {
    }

    override fun deleteAllDiaries() {
        mDatabase.removeValue()

        mCacheDirty = true
    }

    override fun deleteDiary(diaryId: Long) {
        mDatabase.child(diaryId.toString()).removeValue()
    }

    companion object {
        private val diaries_root = "diary"
    }

    /**
     * listen to data change, and parse the data
     */
    private class DatabaseDataChangeListener(val listener: (data: List<Diary>) -> Unit) : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            logThrowable(p0.toException(), "FirebaseDatabase")
        }

        override fun onDataChange(ds: DataSnapshot) {
            ds.value.apply {
                when (this) {
                    is HashMap<*, *> -> {
                        listener(this.map { parseItem(it.value as String) })
                    }
                    is String -> {
                        listener(listOf(parseItem(this)))
                    }
                    else -> {
                        listener(listOf())
                    }
                }
            }
        }

        private fun parseItem(json: String): Diary {
            logd("json = $json")
            return Gson().fromJson(json, Diary::class.java)
        }
    }
}
