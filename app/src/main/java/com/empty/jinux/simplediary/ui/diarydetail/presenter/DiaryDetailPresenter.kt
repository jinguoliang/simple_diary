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
import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.data.source.DiariesDataSource
import com.empty.jinux.simplediary.data.source.DiariesRepository
import com.empty.jinux.simplediary.location.LocationManager
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailContract
import com.empty.jinux.simplediary.util.formatDisplayTime
import com.empty.jinux.simplediary.weather.WeatherManager
import com.google.common.base.Strings
import javax.inject.Inject

/**
 * Listens to user actions from the UI, retrieves the data and updates
 * the UI as required.
 */
internal class DiaryDetailPresenter
/**
 * Dagger strictly enforces that arguments not marked with `@Nullable` are not injected
 * with `@Nullable` values.
 */
@Inject
constructor(
        private val mDiariesRepository: DiariesRepository,
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

    private var currentContent: String? = null

    private val isNewDiary: Boolean
        get() = mDiaryId == null

    override fun start() {
        if (isNewDiary) {
            initForNewDiary()
        } else {
            initForDiary(mDiaryId!!)
        }
    }

    private fun initForDiary(diaryId: String) {
        openDiary(diaryId)
        mDiaryDetailView.showEditButton()
    }

    private fun initForNewDiary() {
        refreshLocation()
        refreshWeather()
        mDiaryDetailView.showSaveButton()
    }

    private fun openDiary(diaryId: String) {
        mDiaryDetailView.setLoadingIndicator(true)
        mDiariesRepository.getDiary(diaryId, object : DiariesDataSource.GetDiaryCallback {
            override fun onDiaryLoaded(diary: Diary) {
                // The view may not be able to handle UI updates anymore
                if (!mDiaryDetailView.isActive) {
                    return
                }

                mDiaryDetailView.setLoadingIndicator(false)
                currentContent = diary.description
                showDiary(diary)
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
        if (Strings.isNullOrEmpty(currentContent)) {
            mDiaryDetailView.showEmptyDiaryError()
            return
        }

        if (isNewDiary) {
            createDiary()
        } else {
            updateDiary()
        }
        mDiaryDetailView.showEditButton()
        mDiaryDetailView.showDiarySaved()
    }

    private fun createDiary() {
        val newDiary = Diary("", currentContent!!)
        if (newDiary.isEmpty) {
            mDiaryDetailView.showEmptyDiaryError()
        } else {
            mDiariesRepository.save(newDiary)
            mDiaryDetailView.showDiarySaved()
        }
    }

    private fun updateDiary() {
        mDiariesRepository.save(Diary("", currentContent!!, mDiaryId!!))
        mDiaryDetailView.showDiarySaved() // After an edit, go back to the list.
    }

    override fun editDiary() {
        mDiaryDetailView.showSaveButton()
    }

    override fun deleteDiary() {
        if (Strings.isNullOrEmpty(mDiaryId)) {
            mDiaryDetailView.showMissingDiary()
            return
        }
        mDiariesRepository.deleteDiary(mDiaryId!!)
        mDiaryDetailView.showDiaryDeleted()
    }

    private fun showDiary(diary: Diary) {
        val description = diary.description

        mDiaryDetailView.showDate(diary.formatDisplayTime())

        if (Strings.isNullOrEmpty(description)) {
            mDiaryDetailView.hideDescription()
        } else {
            mDiaryDetailView.showDescription(description)
        }
    }

    override fun refreshLocation() {
        mLocationManager.getCurrentAddress { address ->
            mDiaryDetailView.showLocation(address)
        }
    }

    override fun refreshWeather() {
        mLocationManager.getLastLocation {
            mWeatherManager.getCurrentWeather(it.latitude, it.longitude) {
                logi("current weather = $it")
                mDiaryDetailView.showWeather(it.description, mWeatherManager.getWeatherIcon(it.icon))
            }
        }
    }

    private var mDiaryId: String? = null

    fun setDiaryId(diaryId: String?) {
        mDiaryId = diaryId
    }

    fun onContentChange(newContent: String) {
        currentContent = newContent
    }
}
