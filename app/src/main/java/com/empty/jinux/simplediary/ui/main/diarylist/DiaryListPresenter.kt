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

import android.app.Activity
import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.data.source.DiariesDataSource
import com.empty.jinux.simplediary.di.Repository
import com.empty.jinux.simplediary.ui.main.MainActivity
import com.empty.jinux.simplediary.util.dayTime
import com.empty.jinux.simplediary.util.today
import com.empty.jinux.simplediary.util.wordsCount
import javax.inject.Inject


/**
 * Listens to user actions from the UI ([DiaryListFragment]), retrieves the data and updates the
 * UI as required.
 *
 *
 * By marking the constructor with `@Inject`, Dagger injects the dependencies required to
 * create an instance of the DiaryListPresenter (if it fails, it emits a compiler error).  It uses
 * [DiaryListPresenterModule] to do so.
 *
 *
 * Dagger generated code doesn't require public access to the constructor or class, and
 * therefore, to ensure the developer doesn't instantiate the class manually and bypasses Dagger,
 * it's good practice minimise the visibility of the class/constructor as much as possible.
 */
internal class DiaryListPresenter
/**
 * Dagger strictly enforces that arguments not marked with `@Nullable` are not injected
 * with `@Nullable` values.
 */
@Inject
constructor(@param:Repository private val mDiariesRepository: DiariesDataSource,
            private val mDiariesView: DiaryListContract.View) : DiaryListContract.Presenter {

    private var mFirstLoad = true

    override fun start() {
        loadDiaries(true)
    }

    override fun stop() {
    }

    override fun result(requestCode: Int, resultCode: Int) {
        if (MainActivity.REQUEST_ADD_DIARY == requestCode && Activity.RESULT_OK == resultCode) {
            mDiariesView.showSuccessfullySavedMessage()
        }
    }

    override fun loadDiaries(forceUpdate: Boolean) {
        // Simplification for sample: a network reload will be forced on first load.
        loadDiaries(forceUpdate || mFirstLoad, true)
        mFirstLoad = false
    }

    var mDiariesCached: List<Diary>? = null

    /**
     * @param forceUpdate   Pass in true to refresh the data in the [DiariesDataSource]
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private fun loadDiaries(forceUpdate: Boolean, showLoadingUI: Boolean) {
        if (showLoadingUI) {
            mDiariesView.setLoadingIndicator(true)
        }
        if (forceUpdate) {
            mDiariesRepository.refreshDiaries()
        }

        mDiariesRepository.getDiaries(object : DiariesDataSource.LoadDiariesCallback {
            override fun onDiariesLoaded(diaries: List<Diary>) {
                mDiariesCached = diaries

                // The view may not be able to handle UI updates anymore
                if (!mDiariesView.isActive) {
                    return
                }
                if (showLoadingUI) {
                    mDiariesView.setLoadingIndicator(false)
                }

                processDiaries(diaries)
            }

            override fun onDataNotAvailable() {
                // The view may not be able to handle UI updates anymore
                if (!mDiariesView.isActive) {
                    return
                }

                if (showLoadingUI) {
                    mDiariesView.setLoadingIndicator(false)
                }
                mDiariesView.showLoadingDiariesError()
            }
        })
    }

    private fun processDiaries(diaries: List<Diary>) {
        if (diaries.isEmpty()) {
            // Show a message indicating there are no diaries for that filter type.
            processEmptyDiaries()
        } else {
            if (mCurrentQuery.isEmpty()) {
                mDiariesView.showDiaries(diaries)
            } else {
                mDiariesView.showDiaries(diaries.filter { it.diaryContent.content.contains(mCurrentQuery) })
            }
        }

    }

    private fun processEmptyDiaries() {
        mDiariesView.showNoDiaries()
    }

    override fun addNewDiary() {
        mDiariesView.showAddDiary(wordCountToday())
    }

    override fun openDiaryDetails(diary: Diary) {
        mDiariesView.showDiaryDetailsUI(diary.id, wordCountToday())
    }

    private fun wordCountToday(): Int {
        return mDiariesCached?.run {
            filter { it.diaryContent.displayTime.dayTime() == today().timeInMillis }
                    .fold(0) { s, d -> s + d.diaryContent.content.wordsCount() }
        } ?: 0
    }

    override fun deleteDiary(diary: Diary) {
        mDiariesRepository.deleteDiaryAsync(diary.id, object : DiariesDataSource.OnCallback<Boolean> {
            override fun onResult(result: Boolean) {
                loadDiaries(true, true)
            }
        })
    }

    private var mCurrentQuery = ""

    override fun searchDiary(query: String) {
        mCurrentQuery = query
        mDiariesCached?.apply { processDiaries(this) }
    }
}
