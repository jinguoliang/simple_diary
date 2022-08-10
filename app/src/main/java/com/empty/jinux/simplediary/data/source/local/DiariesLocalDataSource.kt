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

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.empty.jinux.baselibaray.log.loge
import com.empty.jinux.simplediary.data.*
import com.empty.jinux.simplediary.data.source.DiariesDataSource
import com.empty.jinux.simplediary.data.source.local.room.DATABASE_NAME
import com.empty.jinux.simplediary.data.source.local.room.DiaryDatabase
import com.empty.jinux.simplediary.data.source.local.room.entity.Emotion
import com.empty.jinux.simplediary.data.source.local.room.entity.Location
import com.empty.jinux.simplediary.data.source.local.room.entity.Weather
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Concrete implementation of a data source as a db.
 */
@Singleton
class DiariesLocalDataSource
@Inject
constructor(val context: Context) : DiariesDataSource {

    private var database = Room.databaseBuilder(
        context.applicationContext,
        DiaryDatabase::class.java, DATABASE_NAME
    ).build()

    private var diaryDao = database.diaryDao()

    override suspend fun getDiaries(): List<Diary> {
        val result = try {
            diaryDao.getAll().map { diary ->
                mapDiaryFromRoomToDataSource(diary)
            }
        } catch (e: Throwable) {
            loge(Log.getStackTraceString(e), "DiariesLocalDataSource")
            throw RuntimeException(e)
        }

        val data = result as List<Diary>
        return data.filter { !it.meta.deleted }.sortedBy { it.diaryContent.displayTime }
    }

    override suspend fun getDiary(diaryId: Long): Diary? {
        val diary = diaryDao.getOneById(diaryId) ?: return null
        return mapDiaryFromRoomToDataSource(diary)
    }

    override suspend fun save(diary: Diary): Long {
        loge("save diary", "notice")
        val id = diaryDao.insertOne(mapDiaryFromDataSourceToRoom(diary))
        return id
    }

    override suspend fun refreshDiaries() {
        database.close()
        database = Room.databaseBuilder(
            context.applicationContext,
            DiaryDatabase::class.java, DATABASE_NAME
        ).build()
        diaryDao = database.diaryDao()
    }

    override suspend fun deleteAllDiaries() {
        // todo
    }

    override suspend fun deleteDiary(diaryId: Long): Boolean {
        diaryDao.getOneById(diaryId)?.apply {
            deleted = true
            diaryDao.updateState(this)
        }
        return true
    }


    private fun mapDiaryFromRoomToDataSource(diary: com.empty.jinux.simplediary.data.source.local.room.entity.Diary) =
        Diary(diary.id!!, DiaryContent(
            diary.title,
            diary.contentText,
            diary.displayTime,
            weatherInfo = diary.weather?.run { WeatherInfo(desc, icon) },
            locationInfo = diary.location?.run {
                LocationInfo(
                    com.empty.jinux.simplediary.location.Location(
                        longitude,
                        latitude
                    ), address
                )
            },
            emotionInfo = diary.emotion?.run { EmotionInfo(icon) }
        ), meta = Meta(diary.createTime, diary.lastChangeTime, diary.deleted))

    private fun mapDiaryFromDataSourceToRoom(diary: Diary) =
        com.empty.jinux.simplediary.data.source.local.room.entity.Diary(
            diary.id.takeIf { it != INVALID_DIARY_ID },
            contentText = diary.diaryContent.content, displayTime = diary.diaryContent.displayTime,
            weather = diary.diaryContent.weatherInfo?.run {
                Weather(
                    null, desc = description,
                    icon = icon
                )
            },
            location = diary.diaryContent.locationInfo?.run {
                Location(null, location.latitude, location.longitude, address)
            },
            emotion = diary.diaryContent.emotionInfo?.run { Emotion(null, id) },
            createTime = diary.meta.createdTime,
            lastChangeTime = diary.meta.lastChangeTime,
            deleted = diary.meta.deleted
        )
}
