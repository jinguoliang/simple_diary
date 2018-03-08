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

package com.empty.jinux.simplediary.ui.diarydetail.presenter

import com.empty.jinux.baselibaray.logi
import com.empty.jinux.simplediary.data.*
import com.empty.jinux.simplediary.data.source.DiariesDataSource
import com.empty.jinux.simplediary.di.Repository
import com.empty.jinux.simplediary.location.LocationManager
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailContract
import com.empty.jinux.simplediary.util.formatDateWithWeekday
import com.empty.jinux.simplediary.util.formatDisplayTime
import com.empty.jinux.simplediary.weather.WeatherManager
import javax.inject.Inject

/**
 * Listens to user actions from the UI, retrieves the data and updates
 * the UI as required.
 */
class DiaryDetailPresenter
@Inject
constructor(
        @param:Repository private val mDiariesRepository: DiariesDataSource,
        private val mDiaryDetailView: DiaryDetailContract.View,
        private val mLocationManager: LocationManager,
        private val mWeatherManager: WeatherManager) : DiaryDetailContract.Presenter {

    /**
     * Method injection is used here to safely reference `this` after the object is created.
     * For more information, see Java Concurrency in Practice.
     */
    @Inject
    fun setupListeners() {
        mDiaryDetailView.setPresenter(this)
    }

    private var mDiaryId: Int = INVALID_DIARY_ID
    private var currentDiaryContent: DiaryContent = EMPTY_CONTENT
    private var currentDairyMeta = EMPTY_META

    private val isNewDiary: Boolean
        get() = mDiaryId == INVALID_DIARY_ID

    override fun start() {
        if (isNewDiary) {
            initForNewDiary()
        } else {
            initForDiary()
        }
    }

    private fun initForDiary() {
        openDiary()
        mDiaryDetailView.showEditButton()
    }

    fun initForNewDiary() {
        refreshLocation()
        refreshWeather()
        mDiaryDetailView.showDate(formatDateWithWeekday(System.currentTimeMillis()))
        mDiaryDetailView.showSaveButton()
    }

    private fun openDiary() {
        mDiaryDetailView.setLoadingIndicator(true)
        mDiariesRepository.getDiary(mDiaryId, object : DiariesDataSource.GetDiaryCallback {
            override fun onDiaryLoaded(diary: Diary) {
                // The view may not be able to handle UI updates anymore
                if (!mDiaryDetailView.isActive) {
                    return
                }

                mDiaryDetailView.setLoadingIndicator(false)
                currentDiaryContent = diary.diaryContent
                currentDiaryContent.weatherInfo = diary.diaryContent.weatherInfo
                showDiary()
            }

            override fun onDataNotAvailable() {
                // The view may not be able to handle UI updates anymore
                if (!mDiaryDetailView.isActive) {
                    return
                }
                mDiaryDetailView.showMissingDiary()
            }
        })
    }

    override fun saveDiary() {
        if (isNewDiary) {
            val createdTime = System.currentTimeMillis()
            currentDairyMeta = Meta(createdTime, createdTime)
            currentDiaryContent.displayTime = createdTime
        } else {
            currentDairyMeta.lastChangeTime = System.currentTimeMillis()
        }

        val newDiary = Diary(
                mDiaryId,
                currentDiaryContent,
                currentDairyMeta
        )
        mDiariesRepository.save(newDiary)
        mDiaryDetailView.showEditButton()
        mDiaryDetailView.showDiarySaved()
    }

    override fun editDiary() {
        mDiaryDetailView.showSaveButton()
    }

    override fun deleteDiary() {
        mDiariesRepository.deleteDiary(mDiaryId)
        mDiaryDetailView.showDiaryDeleted()
    }

    private fun showDiary() {
        currentDiaryContent.apply {
            mDiaryDetailView.showDate(formatDisplayTime())
            mDiaryDetailView.showDescription(content)

            weatherInfo?.apply {
                mDiaryDetailView.showWeather(description, iconUri)
            }

            locationInfo?.apply {
                mDiaryDetailView.showLocation(address)
            }
        }
    }

    override fun refreshLocation() {
        mLocationManager.getLastLocation { location ->
            mLocationManager.getCurrentAddress { address ->
                currentDiaryContent.locationInfo = LocationInfo(location, address)
                mDiaryDetailView.showLocation(address)
            }
        }
    }

    override fun refreshWeather() {
        mLocationManager.getLastLocation {
            mWeatherManager.getCurrentWeather(it.latitude, it.longitude) {
                logi("current weatherInfo = $it")
                currentDiaryContent.weatherInfo = WeatherInfo(it.description, it.iconUri())
                mDiaryDetailView.showWeather(it.description, it.iconUri())
            }
        }
    }

    fun setDiaryId(diaryId: Int) {
        mDiaryId = diaryId
    }

    fun onContentChange(newContent: String) {
        currentDiaryContent.content = newContent
    }
}