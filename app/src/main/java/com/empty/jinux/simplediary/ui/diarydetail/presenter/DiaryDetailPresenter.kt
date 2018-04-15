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

import android.support.annotation.UiThread
import android.text.TextUtils
import com.empty.jinux.baselibaray.logi
import com.empty.jinux.simplediary.data.*
import com.empty.jinux.simplediary.data.source.DiariesDataSource
import com.empty.jinux.simplediary.di.Repository
import com.empty.jinux.simplediary.location.LocationManager
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailContract
import com.empty.jinux.simplediary.util.formatDateWithWeekday
import com.empty.jinux.simplediary.util.formatDisplayTime
import com.empty.jinux.simplediary.weather.WeatherManager
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
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

    private var mDiaryId: Long = INVALID_DIARY_ID
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
    }

    private fun initForNewDiary() {
        refreshLocation()
        refreshWeather()
        mDiaryDetailView.showDate(formatDateWithWeekday(System.currentTimeMillis()))
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
                currentDairyMeta = diary.meta
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
        if (isContentCleared()) {
            deleteDiaryFromRepoIfNecessary()
            return
        }

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
        mDiariesRepository.save(newDiary, object : DiariesDataSource.OnCallback<Long> {
            override fun onResult(id: Long) {
                mDiaryId = id
            }

        })
        mDiaryDetailView.showDiarySaved()
    }

    private fun deleteDiaryFromRepoIfNecessary() {
        if (mDiaryId != INVALID_DIARY_ID) {
            mDiariesRepository.deleteDiaryAsync(mDiaryId, object : DiariesDataSource.OnCallback<Boolean> {
                override fun onResult(result: Boolean) {

                }

            })
            mDiaryId = INVALID_DIARY_ID
        }
    }

    private fun isContentCleared() = TextUtils.isEmpty(currentDiaryContent.content)

    override fun editDiary() {
        mDiaryDetailView.showInputMethod()
    }

    override fun deleteDiary() {
        currentDiaryContent.content = ""
        deleteDiaryFromRepoIfNecessary()
        mDiaryDetailView.showDiaryDeleted()
    }

    private fun showDiary() {
        currentDiaryContent.apply {
            mDiaryDetailView.showDate(formatDisplayTime())
            mDiaryDetailView.showContent(content)

            weatherInfo?.apply {
                mDiaryDetailView.showWeather(description, icon)
            }

//            locationInfo?.apply {
//                mDiaryDetailView.showLocation(address)
//            }

            emotionInfo?.apply {
                mDiaryDetailView.showEmotion(id)
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
                currentDiaryContent.weatherInfo = WeatherInfo(it.description, it.icon)
                mDiaryDetailView.showWeather(it.description, it.icon)
            }
        }
    }

    fun setDiaryId(diaryId: Long) {
        mDiaryId = diaryId
    }

    override fun setEmotion(id: Long) {
        currentDiaryContent.emotionInfo = EmotionInfo(id)
    }

    override fun setWeather(iconCode: String) {
        currentDiaryContent.apply {
            weatherInfo = WeatherInfo(weatherInfo?.description ?: "", iconCode)
        }
    }


    fun onContentChange(newContent: String) {
        currentDiaryContent.content = newContent
    }

    override fun stop() {
        mDiaryDetailView.hideInputMethod()
        saveDiary()
    }
}
