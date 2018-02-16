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

package com.empty.jinux.simplediary.diarylist

import android.app.Activity
import com.empty.jinux.simplediary.addeditdiary.AddEditDiaryActivity
import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.data.source.TasksDataSource
import com.empty.jinux.simplediary.data.source.TasksRepository
import com.google.common.base.Preconditions.checkNotNull
import java.util.*
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
constructor(private val mTasksRepository: TasksRepository, private val mTasksView: DiaryListContract.View) : DiaryListContract.Presenter {

    /**
     * Sets the current task filtering type.
     *
     * @param requestType Can be [DiaryListFilterType.ALL],
     * [DiaryListFilterType.COMPLETED], or
     * [DiaryListFilterType.ACTIVE]
     */
    override var filtering = DiaryListFilterType.ALL

    private var mFirstLoad = true

    /**
     * Method injection is used here to safely reference `this` after the object is created.
     * For more information, see Java Concurrency in Practice.
     */
    @Inject
    fun setupListeners() {
        mTasksView.setPresenter(this)
    }

    override fun start() {
        loadTasks(false)
    }

    override fun result(requestCode: Int, resultCode: Int) {
        // If a task was successfully added, show snackbar
        if (AddEditDiaryActivity.REQUEST_ADD_TASK === requestCode && Activity.RESULT_OK == resultCode) {
            mTasksView.showSuccessfullySavedMessage()
        }
    }

    override fun loadTasks(forceUpdate: Boolean) {
        // Simplification for sample: a network reload will be forced on first load.
        loadTasks(forceUpdate || mFirstLoad, true)
        mFirstLoad = false
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the [TasksDataSource]
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private fun loadTasks(forceUpdate: Boolean, showLoadingUI: Boolean) {
        if (showLoadingUI) {
            mTasksView.setLoadingIndicator(true)
        }
        if (forceUpdate) {
            mTasksRepository.refreshTasks()
        }

        mTasksRepository.getDiaries(object : TasksDataSource.LoadDiariesCallback {
            override fun onTasksLoaded(tasks: List<Diary>) {
                val tasksToShow = ArrayList<Diary>()

                // We filter the tasks based on the requestType
                for (task in tasks) {
                    when (filtering) {
                        DiaryListFilterType.ALL -> tasksToShow.add(task)
                        DiaryListFilterType.ACTIVE -> if (task.isActive) {
                            tasksToShow.add(task)
                        }
                        DiaryListFilterType.COMPLETED -> if (task.isCompleted) {
                            tasksToShow.add(task)
                        }
                        else -> tasksToShow.add(task)
                    }
                }
                // The view may not be able to handle UI updates anymore
                if (!mTasksView.isActive) {
                    return
                }
                if (showLoadingUI) {
                    mTasksView.setLoadingIndicator(false)
                }

                processTasks(tasksToShow)
            }

            override fun onDataNotAvailable() {
                // The view may not be able to handle UI updates anymore
                if (!mTasksView.isActive) {
                    return
                }
                mTasksView.showLoadingTasksError()
            }
        })
    }

    private fun processTasks(tasks: List<Diary>) {
        if (tasks.isEmpty()) {
            // Show a message indicating there are no tasks for that filter type.
            processEmptyTasks()
        } else {
            // Show the list of tasks
            mTasksView.showTasks(tasks)
            // Set the filter label's text.
            showFilterLabel()
        }
    }

    private fun showFilterLabel() {
        when (filtering) {
            DiaryListFilterType.ACTIVE -> mTasksView.showActiveFilterLabel()
            DiaryListFilterType.COMPLETED -> mTasksView.showCompletedFilterLabel()
            else -> mTasksView.showAllFilterLabel()
        }
    }

    private fun processEmptyTasks() {
        when (filtering) {
            DiaryListFilterType.ACTIVE -> mTasksView.showNoActiveTasks()
            DiaryListFilterType.COMPLETED -> mTasksView.showNoCompletedTasks()
            else -> mTasksView.showNoTasks()
        }
    }

    override fun addNewTask() {
        mTasksView.showAddTask()
    }

    override fun openTaskDetails(requestedTask: Diary) {
        checkNotNull<Diary>(requestedTask, "requestedTask cannot be null!")
        mTasksView.showTaskDetailsUi(requestedTask.id)
    }

    override fun completeTask(completedTask: Diary) {
        checkNotNull<Diary>(completedTask, "completedTask cannot be null!")
        mTasksRepository.completeTask(completedTask)
        mTasksView.showTaskMarkedComplete()
        loadTasks(false, false)
    }

    override fun activateTask(activeTask: Diary) {
        checkNotNull<Diary>(activeTask, "activeTask cannot be null!")
        mTasksRepository.activateTask(activeTask)
        mTasksView.showTaskMarkedActive()
        loadTasks(false, false)
    }

    override fun clearCompletedTasks() {
        mTasksRepository.clearCompletedTasks()
        mTasksView.showCompletedTasksCleared()
        loadTasks(false, false)
    }

}
