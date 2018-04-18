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

package com.empty.jinux.simplediary.ui.main

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.view.MenuItem
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.intent.helpTranslate
import com.empty.jinux.simplediary.intent.rateApp
import com.empty.jinux.simplediary.intent.sendFeedback
import com.empty.jinux.simplediary.intent.shareApp
import com.empty.jinux.simplediary.ui.about.AboutActivity
import com.empty.jinux.simplediary.ui.main.diarylist.DiaryListFragment
import com.empty.jinux.simplediary.ui.main.statistics.StatisticsFragment
import com.empty.jinux.simplediary.util.ActivityUtils
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.diary_list_act.*
import org.jetbrains.anko.intentFor

class MainActivity : DaggerAppCompatActivity() {

    lateinit var mDiaryListFragment: DiaryListFragment

    lateinit var mStatisticFragment: StatisticsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.diary_list_act)
        setupToolbar()
        setupNavigationDrawer()
        setupFragment()
    }

    private fun setupFragment() {
        val fragment = supportFragmentManager.findFragmentById(R.id.contentFrame)
        when(fragment) {
            is DiaryListFragment -> {
                mDiaryListFragment = fragment
                showDiaryListFragment()
            }
            is StatisticsFragment -> {
                mStatisticFragment = fragment
                showDiaryStatistics()
            }
            else -> {
                mDiaryListFragment = DiaryListFragment.newInstance()
                ActivityUtils.addFragmentToActivity(
                        supportFragmentManager, mDiaryListFragment, R.id.contentFrame)
            }
        }
    }

    private fun setupNavigationDrawer() {
        drawer_layout.setStatusBarBackground(R.color.colorPrimaryDark)
        val navigationView = nav_view
        if (navigationView != null) {
            setupDrawerContent(navigationView)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        val ab = supportActionBar
        ab!!.setHomeAsUpIndicator(R.drawable.ic_menu)
        ab.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.list_navigation_menu_item -> {
                    showDiaryListFragment()
                }
                R.id.statistics_navigation_menu_item -> {
                    showDiaryStatistics()
                }
                R.id.rate_navigation_menu_item -> {
                    startActivity(rateApp(this))
                }
                R.id.share_navigation_menu_item -> {
                    startActivity(shareApp(this))
                }
                R.id.translate_navigation_menu_item -> {
                    startActivity(helpTranslate(this))
                }
                R.id.feedback_navigation_menu_item -> {
                    startActivity(sendFeedback(this))
                }
                R.id.about_navigation_menu_item -> {
                    startActivity(intentFor<AboutActivity>())
                }
                else -> {
                }
            }

            // Do nothing, we're already on that screen
            // Close the navigation drawer when an item is selected.
            menuItem.isChecked = true
            drawer_layout.closeDrawers()
            true
        }
    }

    private fun showDiaryStatistics() {
        val fragment = StatisticsFragment.newInstance()
        mStatisticFragment = fragment
        ActivityUtils.replaceFragment(
                supportFragmentManager, mStatisticFragment, R.id.contentFrame)
    }

    private fun showDiaryListFragment() {
        ActivityUtils.replaceFragment(
                supportFragmentManager, mDiaryListFragment, R.id.contentFrame)
    }

    companion object {
        val REQUEST_ADD_DIARY = 1
        private val CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY"
    }
}
