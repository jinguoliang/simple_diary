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

package com.empty.jinux.simplediary.ui.main.statistics


import com.empty.jinux.simplediary.data.source.diary.Diary
import com.empty.jinux.simplediary.data.source.diary.DiariesDataSource

/**
 * Listens to user actions from the UI ([StatisticsFragment]), retrieves the data and updates
 * the UI as required.
 */
internal class StatisticsPresenter
constructor(private val mTasksRepository: DiariesDataSource,
            private val mStatisticsView: StatisticsContract.View) : StatisticsContract.Presenter {


    override fun start() {
        loadStatistics()
    }

    private fun loadStatistics() {
        mStatisticsView.setProgressIndicator(true)

        mTasksRepository.getDiaries(object : DiariesDataSource.LoadDiariesCallback {
            override fun onDiariesLoaded(diaries: List<Diary>) {
                // The view may not be able to handle UI updates anymore
                if (!mStatisticsView.isActive) {
                    return
                }
                mStatisticsView.setProgressIndicator(false)

                mStatisticsView.showStatistics(diaries)
            }

            override fun onDataNotAvailable() {
                // The view may not be able to handle UI updates anymore
                if (!mStatisticsView.isActive) {
                    return
                }
                mStatisticsView.showLoadingStatisticsError()
            }
        })
    }
}
