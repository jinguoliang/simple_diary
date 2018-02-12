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

package com.empty.jinux.simplediary.tasks

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.empty.jinux.baselibaray.logd
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
import kotlinx.android.synthetic.main.tasks_act.*
import javax.inject.Inject

class TasksActivity : DaggerAppCompatActivity() {

    @Subcomponent
    internal interface Component : AndroidInjector<TasksActivity> {

        @Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<TasksActivity>()
    }

    @dagger.Module(subcomponents = arrayOf(Component::class))
    internal abstract inner class Module {

        @Binds
        @IntoMap
        @ActivityKey(TasksActivity::class)
        internal abstract fun bind(builder: Component.Builder): AndroidInjector.Factory<out Activity>
    }

    private var mDrawerLayout: DrawerLayout? = null

    internal var mTasksPresenter: TasksPresenter? = null

    @Inject
    lateinit internal var mTasksRepository: TasksRepository

    @Inject lateinit var mDog: Dog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasks_act)

        // Set up the toolbar.
        setSupportActionBar(toolbar)
        val ab = supportActionBar
        ab!!.setHomeAsUpIndicator(R.drawable.ic_launcher_background)
        ab.setDisplayHomeAsUpEnabled(true)

        // Set up the navigation drawer.
        mDrawerLayout = drawer_layout
        mDrawerLayout!!.setStatusBarBackground(R.color.colorPrimaryDark)
        val navigationView = nav_view
        if (navigationView != null) {
            setupDrawerContent(navigationView)
        }
        var tasksFragment: TasksFragment? = supportFragmentManager.findFragmentById(R.id.contentFrame) as TasksFragment?
        if (tasksFragment == null) {
            // Create the fragment
            tasksFragment = TasksFragment.newInstance()
            ActivityUtils.addFragmentToActivity(
                    supportFragmentManager, tasksFragment, R.id.contentFrame)
        }

        logd("tasks ${mDog.name}")

        mTasksPresenter = TasksPresenter(mTasksRepository, tasksFragment)
        mTasksPresenter!!.setupListeners()

        // Load previously saved state, if available.
        if (savedInstanceState != null) {
            val currentFiltering = savedInstanceState.getSerializable(CURRENT_FILTERING_KEY) as TasksFilterType
            mTasksPresenter!!.filtering = currentFiltering
        }
    }

    public override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putSerializable(CURRENT_FILTERING_KEY, mTasksPresenter!!.filtering)

        super.onSaveInstanceState(outState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Open the navigation drawer when the home icon is selected from the toolbar.
                mDrawerLayout!!.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.list_navigation_menu_item -> {
                }
                R.id.statistics_navigation_menu_item -> {
//                    val intent = Intent(this@TasksActivity, StatisticsActivity::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                    startActivity(intent)
                }
                else -> {
                }
            }// Do nothing, we're already on that screen
            // Close the navigation drawer when an item is selected.
            menuItem.isChecked = true
            mDrawerLayout!!.closeDrawers()
            true
        }
    }

    companion object {

        private val CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY"
    }
}
