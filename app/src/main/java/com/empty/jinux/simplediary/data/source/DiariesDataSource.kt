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
 * Main entry point for accessing diaries data
 */
interface DiariesDataSource {

    suspend fun getDiaries(): List<Diary>

    suspend fun getDiary(diaryId: Long): Diary?

    suspend fun save(diary: Diary):Long

    suspend fun refreshDiaries()

    suspend fun deleteAllDiaries()

    suspend fun deleteDiary(diaryId: Long): Boolean
}
