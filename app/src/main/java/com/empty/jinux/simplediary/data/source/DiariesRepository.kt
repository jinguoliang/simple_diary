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
import com.empty.jinux.simplediary.di.EmptyData
import com.empty.jinux.simplediary.di.Local
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation to load diaries from the data sources into a cache.
 *
 */
@Singleton
class DiariesRepository
@Inject
internal constructor(
    @param:EmptyData private val mRemoteDataSource: DiariesDataSource,
    @param:Local private val mLocalDataSource: DiariesDataSource
) : DiariesDataSource {


    override suspend fun getDiaries(): List<Diary> {
        // Query the local storage if available. If not, query the network.
        return mLocalDataSource.getDiaries()

//        mRemoteDataSource.getDiaries(object : DiariesDataSource.LoadDiariesCallback {
//            override fun onDiariesLoaded(diaries: List<Diary>) {
//                callback.onDiariesLoaded(diaries)
//            }
//
//            override fun onDataNotAvailable() {
//                callback.onDataNotAvailable()
//            }
//        })
    }

    override suspend fun save(diary: Diary): Long {
        mRemoteDataSource.save(diary)

        return mLocalDataSource.save(diary)
    }

    override suspend fun getDiary(diaryId: Long): Diary? {

        // Is the task in the local data source? If not, query the network.
        return mLocalDataSource.getDiary(diaryId)
    }

    override suspend fun refreshDiaries() {
        mLocalDataSource.refreshDiaries()
    }

    override suspend fun deleteAllDiaries() {
        mRemoteDataSource.deleteAllDiaries()
        mLocalDataSource.deleteAllDiaries()
    }

    override suspend fun deleteDiary(diaryId: Long): Boolean {

        mRemoteDataSource.deleteDiary(diaryId)
        mLocalDataSource.deleteDiary(diaryId)
        return true
    }

}
