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

package com.empty.jinux.simplediary.ui.statistics


import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.data.source.TasksDataSource
import com.empty.jinux.simplediary.data.source.TasksRepository
import javax.inject.Inject

/**
 * Listens to user actions from the UI ([StatisticsFragment]), retrieves the data and updates
 * the UI as required.
 *
 *
 * By marking the constructor with `@Inject`, Dagger injects the dependencies required to
 * create an instance of the StatisticsPresenter (if it fails, it emits a compiler error). It uses
 * [StatisticsPresenterModule] to do so.
 *
 *
 * Dagger generated code doesn't require public access to the constructor or class, and
 * therefore, to ensure the developer doesn't instantiate the class manually and bypasses Dagger,
 * it's good practice minimise the visibility of the class/constructor as much as possible.
 */
internal class StatisticsPresenter
/**
 * Dagger strictly enforces that arguments not marked with `@Nullable` are not injected
 * with `@Nullable` values.
 */
@Inject
constructor(private val mTasksRepository: TasksRepository,
            private val mStatisticsView: StatisticsContract.View) : StatisticsContract.Presenter {

    /**
     * Method injection is used here to safely reference `this` after the object is created.
     * For more information, see Java Concurrency in Practice.
     */
    @Inject
    fun setupListeners() {
        mStatisticsView.setPresenter(this)
    }

    override fun start() {
        loadStatistics()
    }

    private fun loadStatistics() {
        mStatisticsView.setProgressIndicator(true)

        mTasksRepository.getDiaries(object : TasksDataSource.LoadDiariesCallback {
            override fun onTasksLoaded(tasks: List<Diary>) {
                var activeTasks = 0
                var completedTasks = 0

                // We calculate number of active and completed tasks
                for ((_, _, _, isCompleted) in tasks) {
                    if (isCompleted) {
                        completedTasks += 1
                    } else {
                        activeTasks += 1
                    }
                }
                // The view may not be able to handle UI updates anymore
                if (!mStatisticsView.isActive) {
                    return
                }
                mStatisticsView.setProgressIndicator(false)

                mStatisticsView.showStatistics(activeTasks, completedTasks)
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
