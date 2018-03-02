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

package com.empty.jinux.simplediary.ui.diarylist

import android.app.Activity
import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.data.source.DiariesDataSource
import com.empty.jinux.simplediary.data.source.DiariesRepository
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
constructor(private val mDiariesRepository: DiariesRepository, private val mDiariesView: DiaryListContract.View) : DiaryListContract.Presenter {

    private var mFirstLoad = true

    /**
     * Method injection is used here to safely reference `this` after the object is created.
     * For more information, see Java Concurrency in Practice.
     */
    @Inject
    fun setupListeners() {
        mDiariesView.setPresenter(this)
    }

    override fun start() {
        loadDiaries(true)
    }

    override fun stop() {
    }

    override fun result(requestCode: Int, resultCode: Int) {
        // If a diary was successfully added, show snackbar
        if (DiaryListActivity.REQUEST_ADD_DIARY == requestCode && Activity.RESULT_OK == resultCode) {
            mDiariesView.showSuccessfullySavedMessage()
        }
    }

    override fun loadDiaries(forceUpdate: Boolean) {
        // Simplification for sample: a network reload will be forced on first load.
        loadDiaries(forceUpdate || mFirstLoad, true)
        mFirstLoad = false
    }

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
                mDiariesView.showLoadingDiariesError()
            }
        })
    }

    private fun processDiaries(diaries: List<Diary>) {
        if (diaries.isEmpty()) {
            // Show a message indicating there are no diaries for that filter type.
            processEmptyDiaries()
        } else {
            // Show the list of diaries
            mDiariesView.showDiaries(diaries)
        }

    }

    private fun processEmptyDiaries() {
        mDiariesView.showNoDiaries()
    }

    override fun addNewDiary() {
        mDiariesView.showAddDiary()
    }

    override fun openDiaryDetails(diary: Diary) {
        mDiariesView.showDiaryDetailsUI(diary.id!!)
    }
}
