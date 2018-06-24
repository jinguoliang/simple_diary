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

package com.empty.jinux.simplediary.ui.diarydetail

import com.empty.jinux.simplediary.BasePresenter
import com.empty.jinux.simplediary.BaseView
import com.empty.jinux.simplediary.data.LocationInfo


/**
 * This specifies the contract between the view and the presenter.
 */
interface DiaryDetailContract {

    interface View : BaseView<Presenter> {

        val isActive: Boolean

        fun setLoadingIndicator(active: Boolean)

        fun showMissingDiary()

        fun hideDescription()

        fun showContent(content: String)

        fun showDiaryDeleted()

        fun showDate(dateStr: String)

        fun showLocation(location: LocationInfo)

        fun showWeather(weather: String, weatherIconUrl: String)

        fun showEmotion(id: Long)

        fun showDiarySaved()

        fun showEmptyDiaryError()

        fun showInputMethod()
        fun hideInputMethod()

        fun hasLocationPermission(): Boolean

        fun shareContent(content: String)

    }

    interface Presenter : BasePresenter {

        fun editDiary()

        fun deleteDiary()

        fun refreshLocation()

        fun saveDiary()

        fun refreshWeather()

        fun stop()

        fun setEmotion(id: Long)

        fun setWeather(icon: String)

        fun setLocation(locationInfo: LocationInfo)

    }
}
