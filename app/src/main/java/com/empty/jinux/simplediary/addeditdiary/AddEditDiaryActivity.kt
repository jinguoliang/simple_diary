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

package com.empty.jinux.simplediary.addeditdiary

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
import kotlinx.android.synthetic.main.addtask_act.*
import javax.inject.Inject

/**
 * Displays an add or edit task screen.
 */
class AddEditDiaryActivity : DaggerAppCompatActivity() {

    @Subcomponent
    internal interface Component : AndroidInjector<AddEditDiaryActivity> {

        @Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<AddEditDiaryActivity>()
    }

    @dagger.Module(subcomponents = arrayOf(Component::class))
    internal abstract inner class Module {

        @Binds
        @IntoMap
        @ActivityKey(AddEditDiaryActivity::class)
        internal abstract fun bind(builder: Component.Builder): AndroidInjector.Factory<out Activity>
    }

    internal lateinit var mAddEditTasksPresenter: AddEditDiaryPresenter

    @Inject lateinit var mRepository: TasksRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addtask_act)

        // Set up the toolbar.
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        var addEditTaskFragment: AddEditDiaryFragment? = supportFragmentManager.findFragmentById(R.id.contentFrame) as AddEditDiaryFragment?

        val taskId = intent.getStringExtra(AddEditDiaryFragment.ARGUMENT_EDIT_TASK_ID)

        if (addEditTaskFragment == null) {
            addEditTaskFragment = AddEditDiaryFragment.newInstance()

            if (intent.hasExtra(AddEditDiaryFragment.ARGUMENT_EDIT_TASK_ID)) {
                actionBar.setTitle(R.string.edit_task)
                val bundle = Bundle()
                bundle.putString(AddEditDiaryFragment.ARGUMENT_EDIT_TASK_ID, taskId)
                addEditTaskFragment!!.arguments = bundle
            } else {
                actionBar.setTitle(R.string.add_task)
            }

            ActivityUtils.addFragmentToActivity(supportFragmentManager,
                    addEditTaskFragment, R.id.contentFrame)
        }

        mAddEditTasksPresenter = AddEditDiaryPresenter(taskId, mRepository, addEditTaskFragment )
        mAddEditTasksPresenter.setupListeners()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {

        val REQUEST_ADD_TASK = 1
    }
}
