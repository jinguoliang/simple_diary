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

package com.empty.jinux.simplediary.ui.main.diarylist

import com.empty.jinux.simplediary.BasePresenter
import com.empty.jinux.simplediary.BaseView
import com.empty.jinux.simplediary.data.Diary

/**
 * This specifies the contract between the view and the presenter.
 */
interface DiaryListContract {

    interface View : BaseView<Presenter> {

        val isActive: Boolean

        fun setLoadingIndicator(active: Boolean)

        fun showDiaries(diaries: List<Diary>)

        fun showAddDiary(todayWords: Int)

        fun showDiaryDetailsUI(diaryId: Long, todayWords: Int)

        fun showLoadingDiariesError()

        fun showNoDiaries()

        fun showSuccessfullySavedMessage()

    }

    interface Presenter : BasePresenter {

        fun result(requestCode: Int, resultCode: Int)

        fun loadDiaries(forceUpdate: Boolean)

        fun addNewDiary()

        fun openDiaryDetails(diary: Diary)

        fun stop()

        fun deleteDiary(diary: Diary)

        fun searchDiary(query: String)

        fun onDestory()
    }
}
