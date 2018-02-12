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

package com.empty.jinux.simplediary.addedittask

import android.app.Activity
import android.os.Bundle
import com.empty.jinux.baselibaray.loge
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.source.TasksRepository
import com.empty.jinux.simplediary.model.Dog
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
class AddEditTaskActivity : DaggerAppCompatActivity() {

    @Subcomponent
    internal interface Component : AndroidInjector<AddEditTaskActivity> {

        @Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<AddEditTaskActivity>()
    }

    @dagger.Module(subcomponents = arrayOf(Component::class))
    internal abstract inner class Module {

        @Binds
        @IntoMap
        @ActivityKey(AddEditTaskActivity::class)
        internal abstract fun bind(builder: Component.Builder): AndroidInjector.Factory<out Activity>
    }

    internal lateinit var mAddEditTasksPresenter: AddEditTaskPresenter

    @Inject lateinit var mRepository: TasksRepository

    @Inject lateinit var mDog: Dog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addtask_act)

        loge(mDog.name)
        // Set up the toolbar.
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        var addEditTaskFragment: AddEditTaskFragment? = supportFragmentManager.findFragmentById(R.id.contentFrame) as AddEditTaskFragment?

        val taskId = intent.getStringExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID)

        if (addEditTaskFragment == null) {
            addEditTaskFragment = AddEditTaskFragment.newInstance()

            if (intent.hasExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID)) {
                actionBar.setTitle(R.string.edit_task)
                val bundle = Bundle()
                bundle.putString(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId)
                addEditTaskFragment!!.arguments = bundle
            } else {
                actionBar.setTitle(R.string.add_task)
            }

            ActivityUtils.addFragmentToActivity(supportFragmentManager,
                    addEditTaskFragment, R.id.contentFrame)
        }

        mAddEditTasksPresenter = AddEditTaskPresenter(taskId, mRepository, addEditTaskFragment )
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
