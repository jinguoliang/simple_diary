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

package com.empty.jinux.simplediary.ui.taskdetail.presenter

import com.empty.jinux.baselibaray.logi
import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.data.source.TasksDataSource
import com.empty.jinux.simplediary.data.source.TasksRepository
import com.empty.jinux.simplediary.location.LocationManager
import com.empty.jinux.simplediary.ui.taskdetail.TaskDetailContract
import com.empty.jinux.simplediary.util.formatDisplayTime
import com.empty.jinux.simplediary.weather.WeatherManager
import com.google.common.base.Strings
import javax.inject.Inject

/**
 * Listens to user actions from the UI ([TaskDetailFragment]), retrieves the data and updates
 * the UI as required.
 *
 *
 * By marking the constructor with `@Inject`, Dagger injects the dependencies required to
 * create an instance of the TaskDetailPresenter (if it fails, it emits a compiler error). It uses
 * [TaskDetailPresenterModule] to do so.
 *
 *
 * Dagger generated code doesn't require public access to the constructor or class, and
 * therefore, to ensure the developer doesn't instantiate the class manually and bypasses Dagger,
 * it's good practice minimise the visibility of the class/constructor as much as possible.
 */
internal class TaskDetailPresenter
/**
 * Dagger strictly enforces that arguments not marked with `@Nullable` are not injected
 * with `@Nullable` values.
 */
@Inject
constructor(
        private val mTasksRepository: TasksRepository,
        private val mTaskDetailView: TaskDetailContract.View,
        private val mLocationManager: LocationManager,
        private val mWeatherManager: WeatherManager) : TaskDetailContract.Presenter {

    /**
     * Method injection is used here to safely reference `this` after the object is created.
     * For more information, see Java Concurrency in Practice.
     */
    @Inject
    fun setupListeners() {
        mTaskDetailView.setPresenter(this)
    }

    private var currentContent: String? = null

    private val isNewTask: Boolean
        get() = mDiaryId == null

    override fun start() {
        if (isNewTask) {
            initForNewDiary()
        } else {
            initForDiary(mDiaryId!!)
        }
    }

    private fun initForDiary(diaryId: String) {
        openDiary()
        mTaskDetailView.showEditButton()
    }

    private fun initForNewDiary() {
        refreshLocation()
        refreshWeather()
        mTaskDetailView.showSaveButton()
    }

    private fun openDiary() {
        mTaskDetailView.setLoadingIndicator(true)
        mTasksRepository.getTask(mDiaryId!!, object : TasksDataSource.GetTaskCallback {
            override fun onTaskLoaded(diary: Diary) {
                // The view may not be able to handle UI updates anymore
                if (!mTaskDetailView.isActive) {
                    return
                }

                mTaskDetailView.setLoadingIndicator(false)
                currentContent = diary.description
                showDiary(diary)
            }

            override fun onDataNotAvailable() {
                // The view may not be able to handle UI updates anymore
                if (!mTaskDetailView.isActive) {
                    return
                }
                mTaskDetailView.showMissingTask()
            }
        })
    }

    override fun saveDiary() {
        if (Strings.isNullOrEmpty(currentContent)) {
            mTaskDetailView.showEmptyTaskError()
            return
        }

        if (isNewTask) {
            createTask()
        } else {
            updateTask()
        }
        mTaskDetailView.showEditButton()
        mTaskDetailView.showTaskSaved()
    }

    private fun createTask() {
        val newTask = Diary("", currentContent!!)
        if (newTask.isEmpty) {
            mTaskDetailView.showEmptyTaskError()
        } else {
            mTasksRepository.save(newTask)
            mTaskDetailView.showTaskSaved()
        }
    }

    private fun updateTask() {
        mTasksRepository.save(Diary("", currentContent!!, mDiaryId!!))
        mTaskDetailView.showTaskSaved() // After an edit, go back to the list.
    }

    override fun editDiary() {
        mTaskDetailView.showSaveButton()
    }

    override fun deleteDiary() {
        if (Strings.isNullOrEmpty(mDiaryId)) {
            mTaskDetailView.showMissingTask()
            return
        }
        mTasksRepository.deleteTask(mDiaryId!!)
        mTaskDetailView.showTaskDeleted()
    }

    private fun showDiary(diary: Diary) {
        val description = diary.description

        mTaskDetailView.showDate(diary.formatDisplayTime())

        if (Strings.isNullOrEmpty(description)) {
            mTaskDetailView.hideDescription()
        } else {
            mTaskDetailView.showDescription(description)
        }
    }

    override fun refreshLocation() {
        mLocationManager.getCurrentAddress { address ->
            mTaskDetailView.showLocation(address)
        }
    }

    override fun refreshWeather() {
        mLocationManager.getLastLocation {
            mWeatherManager.getCurrentWeather(it.latitude, it.longitude) {
                logi("current weather = $it")
                mTaskDetailView.showWeather(it.description, mWeatherManager.getWeatherIcon(it.icon))
            }
        }
    }

    private var mDiaryId: String? = null

    fun setDiaryId(taskId: String?) {
        mDiaryId = taskId
    }

    fun onContentChange(newContent: String) {
        currentContent = newContent
    }
}
