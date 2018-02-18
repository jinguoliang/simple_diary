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

package com.empty.jinux.simplediary.taskdetail

import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.data.source.TasksDataSource
import com.empty.jinux.simplediary.data.source.TasksRepository
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
        /**
         * Dagger strictly enforces that arguments not marked with `@Nullable` are not injected
         * with `@Nullable` values.
         */
        var mTaskId: String?,
        private val mTasksRepository: TasksRepository,
        private val mTaskDetailView: TaskDetailContract.View) : TaskDetailContract.Presenter {

    /**
     * Method injection is used here to safely reference `this` after the object is created.
     * For more information, see Java Concurrency in Practice.
     */
    @Inject
    fun setupListeners() {
        mTaskDetailView.setPresenter(this)
    }

    override fun start() {
        openTask()
    }

    private fun openTask() {
        if (Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailView.showMissingTask()
            return
        }

        mTaskDetailView.setLoadingIndicator(true)
        mTasksRepository.getTask(mTaskId!!, object : TasksDataSource.GetTaskCallback {
            override fun onTaskLoaded(task: Diary?) {
                // The view may not be able to handle UI updates anymore
                if (!mTaskDetailView.isActive) {
                    return
                }
                mTaskDetailView.setLoadingIndicator(false)
                if (null == task) {
                    mTaskDetailView.showMissingTask()
                } else {
                    showTask(task)
                }
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

    override fun editTask() {
        if (Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailView.showMissingTask()
            return
        }
        mTaskDetailView.showEditTask(mTaskId!!)
    }

    override fun deleteTask() {
        if (Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailView.showMissingTask()
            return
        }
        mTasksRepository.deleteTask(mTaskId!!)
        mTaskDetailView.showTaskDeleted()
    }

    override fun completeTask() {
        if (Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailView.showMissingTask()
            return
        }
        mTasksRepository.completeTask(mTaskId!!)
        mTaskDetailView.showTaskMarkedComplete()
    }

    override fun activateTask() {
        if (Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailView.showMissingTask()
            return
        }
        mTasksRepository.activateTask(mTaskId!!)
        mTaskDetailView.showTaskMarkedActive()
    }

    private fun showTask(task: Diary) {
        val title = task.title
        val description = task.description

        if (Strings.isNullOrEmpty(title)) {
            mTaskDetailView.hideTitle()
        } else {
            mTaskDetailView.showTitle(title)
        }

        if (Strings.isNullOrEmpty(description)) {
            mTaskDetailView.hideDescription()
        } else {
            mTaskDetailView.showDescription(description)
        }
        mTaskDetailView.showCompletionStatus(task.isCompleted)
    }
}
