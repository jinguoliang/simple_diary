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

import android.arch.persistence.room.Room
import android.content.Context
import com.empty.jinux.simplediary.data.*
import com.empty.jinux.simplediary.data.source.DiariesDataSource
import com.empty.jinux.simplediary.data.source.local.room.DATABASE_NAME
import com.empty.jinux.simplediary.data.source.local.room.DiaryDatabase
import com.empty.jinux.simplediary.data.source.local.room.entity.Location
import com.empty.jinux.simplediary.data.source.local.room.entity.Weather
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Concrete implementation of a data source as a db.
 */
@Singleton
class DiariesLocalDataSource
@Inject
constructor(context: Context) : DiariesDataSource {

    private var diaryDao = Room.databaseBuilder(context.applicationContext,
            DiaryDatabase::class.java, DATABASE_NAME).build().diaryDao()

    override fun getDiaries(callback: DiariesDataSource.LoadDiariesCallback) {
        doAsync {
            val data = diaryDao.getAll().map { diary ->
                mapDiaryFromRoomToDataSource(diary)
            }
            uiThread {
                callback.onDiariesLoaded(data)
            }
        }
    }

    override fun getDiary(diaryId: Int, callback: DiariesDataSource.GetDiaryCallback) {
        doAsync {
            val diary = diaryDao.getOneById(diaryId)

            uiThread {
                if (diary != null) {
                    callback.onDiaryLoaded(mapDiaryFromRoomToDataSource(diary))
                } else {
                    callback.onDataNotAvailable()
                }
            }

        }
    }

    override fun save(diary: Diary) {
        doAsync {
            diaryDao.insertOne(mapDiaryFromDataSourceToRoom(diary))
        }
    }

    override fun refreshDiaries() {
    }

    override fun deleteAllDiaries() {
        doAsync {
            diaryDao.deleteAll()
        }
    }

    override fun deleteDiary(diaryId: Int) {
        doAsync {
            diaryDao.deleteById(diaryId)
        }
    }

    private fun mapDiaryFromRoomToDataSource(diary: com.empty.jinux.simplediary.data.source.local.room.entity.Diary) =
            Diary(diary.id!!, DiaryContent(
                    diary.title,
                    diary.contentText,
                    diary.displayTime,
                    weatherInfo = diary.weather?.run { WeatherInfo(desc, icon) },
                    locationInfo = diary.location?.run { LocationInfo(com.empty.jinux.simplediary.location.Location(longitude, latitude), address) }
            ), EMPTY_META)

    private fun mapDiaryFromDataSourceToRoom(diary: Diary) =
            com.empty.jinux.simplediary.data.source.local.room.entity.Diary(diary.id.takeIf { it != INVALID_DIARY_ID },
                    contentText = diary.diaryContent.content, displayTime = diary.diaryContent.displayTime,
                    weather = diary.diaryContent.weatherInfo?.run {
                        Weather(null, desc = description,
                                icon = iconUri)
                    },
                    location = diary.diaryContent.locationInfo?.run {
                        Location(null, location.latitude, location.longitude, address)
                    })
}
