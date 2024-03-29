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

import android.os.Bundle
import android.text.TextUtils
import com.empty.jinux.baselibaray.log.logi
import com.empty.jinux.baselibaray.thread.ThreadPools
import com.empty.jinux.baselibaray.utils.*
import com.empty.jinux.simplediary.data.*
import com.empty.jinux.simplediary.data.source.DiariesDataSource
import com.empty.jinux.simplediary.di.Repository
import com.empty.jinux.simplediary.location.LocationManager
import com.empty.jinux.simplediary.report.Reporter
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailContract
import com.empty.jinux.simplediary.ui.diarydetail.fragment.DiaryDetailFragment
import com.empty.jinux.simplediary.ui.diarydetail.fragment.MyEmotionIcons
import com.empty.jinux.simplediary.weather.WeatherManager
import kotlinx.coroutines.*
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

/**
 * Listens to user actions from the UI, retrieves the data and updates
 * the UI as required.
 */
class DiaryDetailPresenter
constructor(
        private val mDiariesRepository: DiariesDataSource,
        private val mDiaryDetailView: DiaryDetailContract.View,
        private val mLocationManager: LocationManager,
        private val mWeatherManager: WeatherManager,
        private val mReporter: Reporter) : DiaryDetailContract.Presenter {

    private val job = Job()
    private val presenterScope = CoroutineScope(Dispatchers.Main + job)

    private var mDiaryId: Long = INVALID_DIARY_ID
    private var currentDiaryContent = DiaryContent("", "", -1, null, null)
    private var currentDairyMeta = Meta(-1, -1, false)

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
        mDiaryDetailView.showEmotion(MyEmotionIcons.getEmotion(0).toLong())
        mDiaryDetailView.showDate(formatDateWithWeekday(System.currentTimeMillis()))

        if (mDiaryDetailView.hasLocationPermission()) {
            refreshLocation()
            refreshWeather()
        }
        ThreadPools.postOnUIDelayed(200) {
            if (mDiaryDetailView.isActive) {
                mDiaryDetailView.showInputMethod()
            }
        }

        mLoadFinished = true
        mShowGoodViewHelper.isToday = true
        mShowGoodViewHelper.init(currentDiaryContent.content.wordsCount())
    }

    private var mLoadFinished = false

    private fun openDiary() {
        mDiaryDetailView.setLoadingIndicator(true)
        presenterScope.launch(Dispatchers.IO) {
           val diary =  mDiariesRepository.getDiary(mDiaryId,)
            withContext(Dispatchers.Main) {
                mLoadFinished = true

                // The view may not be able to handle UI updates anymore
                if (!job.isCancelled) {
                    return@withContext
                }

                if (diary != null) {
                    mDiaryDetailView.setLoadingIndicator(false)
                    currentDiaryContent = diary.diaryContent
                    currentDiaryContent.weatherInfo = diary.diaryContent.weatherInfo
                    currentDiaryContent.locationInfo = diary.diaryContent.locationInfo
                    currentDairyMeta = diary.meta
                    showDiary()
                } else {
                    mDiaryDetailView.showMissingDiary()
                }
            }
        }
    }

    override fun saveDiary() {
        if (isContentCleared()) {
            deleteDiaryFromRepoIfNecessary()
            return
        }

        mReporter.reportCount("words", currentDiaryContent.content.wordsCount())
        presenterScope.launch(Dispatchers.IO) {

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
            withContext(Dispatchers.Main) {
                mDiaryDetailView.showDiarySaved()
            }
        }
    }

    private fun deleteDiaryFromRepoIfNecessary() {
        if (mDiaryId != INVALID_DIARY_ID) {
            presenterScope.launch {
                mDiariesRepository.deleteDiary(mDiaryId, )
            }
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
            mShowGoodViewHelper.isToday = displayTime.toCalendar().setToDayStart() == today()
            mShowGoodViewHelper.init(currentDiaryContent.content.wordsCount())

            weatherInfo?.apply {
                mDiaryDetailView.showWeather(description, icon)
            }

            locationInfo?.apply {
                mDiaryDetailView.showLocation(this)
            }

            emotionInfo?.apply {
                mDiaryDetailView.showEmotion(id)
            }
        }
    }

    override fun refreshLocation() {
        ThreadPools.postOnQuene {
            mLocationManager.getLastLocation { location ->
                mLocationManager.getCurrentAddress { address ->
                    if (!mDiaryDetailView.isActive) return@getCurrentAddress
                    currentDiaryContent.locationInfo = LocationInfo(location, address)
                    ThreadPools.postOnUI {
                        mDiaryDetailView.showLocation(currentDiaryContent.locationInfo!!)
                    }
                }
            }
        }

    }

    override fun refreshWeather() {
        ThreadPools.postOnQuene {
            mLocationManager.getLastLocation {
                mWeatherManager.getCurrentWeather(it.latitude, it.longitude) { weather ->
                    logi("current weatherInfo = $weather")
                    if (!mDiaryDetailView.isActive) return@getCurrentWeather
                    currentDiaryContent.weatherInfo = WeatherInfo(weather.description, weather.icon)
                    ThreadPools.postOnUI {
                        mDiaryDetailView.showWeather(weather.description, weather.icon)
                    }
                }
            }
        }
    }

    fun setDiaryId(diaryId: Long) {
        mDiaryId = diaryId
    }

    private lateinit var mShowGoodViewHelper: ShowGoodViewHelper

    /**
     * must called after init the presenter
     */
    fun setWordCountToday(wordCountToday: Int) {
        mShowGoodViewHelper = ShowGoodViewHelper(wordCountToday, object : ShowGoodViewHelper.Listener {
            override fun onShowGoodViewAnim() {
                mDiaryDetailView.showGoodView()
            }

            override fun onShowGood() {
                mDiaryDetailView.setTodayGood(true)
            }

            override fun onHideGood() {
                mDiaryDetailView.setTodayGood(false)
            }
        })
    }

    override fun setEmotion(id: Long) {
        currentDiaryContent.emotionInfo = EmotionInfo(id)
    }

    override fun setWeather(icon: String) {
        currentDiaryContent.apply {
            weatherInfo = WeatherInfo(weatherInfo?.description ?: "", icon)
        }
    }

    override fun setLocation(locationInfo: LocationInfo) {
        if (currentDiaryContent.locationInfo == locationInfo) return

        currentDiaryContent.locationInfo = locationInfo
        mDiaryDetailView.showLocation(locationInfo)
    }


    fun onContentChange(newContent: String) {
        currentDiaryContent.content = newContent

        mShowGoodViewHelper.updateCurrentArticleWordCount(newContent.wordsCount())
    }

    override fun stop() {
        mDiaryDetailView.hideInputMethod()
        if (mLoadFinished) {
            saveDiary()
        }
    }

    fun shareContent() {
        mDiaryDetailView.shareContent(currentDiaryContent.content)
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putLong(DiaryDetailFragment.KEY_DIARY_ID, mDiaryId)
    }


    override fun onDestory() {
        job.cancel()
    }
}
