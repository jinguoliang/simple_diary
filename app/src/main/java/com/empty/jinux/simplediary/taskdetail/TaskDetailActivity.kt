/*
 * Copyright (C) 2015 The Android Open Source Project
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

import android.app.Activity
import android.os.Bundle
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.source.TasksRepository
import com.empty.jinux.simplediary.util.ActivityUtils
import dagger.Binds
import dagger.Subcomponent
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import dagger.multibindings.IntoMap
import kotlinx.android.synthetic.main.statistics_act.*
import javax.inject.Inject

/**
 * Displays task details screen.
 */
class TaskDetailActivity : DaggerAppCompatActivity() {

    @Subcomponent
    internal interface Component : AndroidInjector<TaskDetailActivity> {

        @Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<TaskDetailActivity>()
    }

    @dagger.Module(subcomponents = arrayOf(Component::class))
    internal abstract inner class Module {

        @Binds
        @IntoMap
        @ActivityKey(TaskDetailActivity::class)
        internal abstract fun bind(builder: Component.Builder): AndroidInjector.Factory<out Activity>
    }

    private var mTaskDetailPresenter: TaskDetailPresenter? = null

    @Inject
    lateinit internal var mTasksRepository: TasksRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.taskdetail_act)

        // Set up the toolbar.
        setSupportActionBar(toolbar)
        val ab = supportActionBar
        ab!!.setDisplayHomeAsUpEnabled(true)
        ab.setDisplayShowHomeEnabled(true)

        // Get the requested task id
        val taskId = intent.getStringExtra(EXTRA_TASK_ID)

        var taskDetailFragment = supportFragmentManager
                .findFragmentById(R.id.contentFrame) as TaskDetailFragment?

        if (taskDetailFragment == null) {
            taskDetailFragment = TaskDetailFragment.newInstance(taskId)

            ActivityUtils.addFragmentToActivity(supportFragmentManager,
                    taskDetailFragment, R.id.contentFrame)
        }

        mTaskDetailPresenter = TaskDetailPresenter(taskId, mTasksRepository, taskDetailFragment)
        mTaskDetailPresenter!!.setupListeners()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {

        val EXTRA_TASK_ID = "TASK_ID"
    }
}
