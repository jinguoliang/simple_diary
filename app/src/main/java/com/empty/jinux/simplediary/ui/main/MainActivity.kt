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

import android.content.ActivityNotFoundException
import android.os.Bundle
import androidx.core.view.GravityCompat
import android.view.MenuItem
import android.view.View
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.intent.helpTranslate
import com.empty.jinux.simplediary.intent.rateApp
import com.empty.jinux.simplediary.intent.sendFeedback
import com.empty.jinux.simplediary.intent.shareApp
import com.empty.jinux.simplediary.report.Reporter
import com.empty.jinux.simplediary.ui.LockHelper
import com.empty.jinux.simplediary.ui.about.AboutActivity
import com.empty.jinux.simplediary.ui.main.diarylist.DiaryListFragment
import com.empty.jinux.simplediary.ui.main.metercouner.MeterCounterFragment
import com.empty.jinux.simplediary.ui.main.statistics.StatisticsFragment
import com.empty.jinux.simplediary.ui.settings.SettingsActivity
import com.empty.jinux.simplediary.util.ActivityUtils
import com.google.android.material.navigation.NavigationView
import dagger.android.support.DaggerAppCompatActivity
import io.multimoon.colorful.BaseTheme
import io.multimoon.colorful.Colorful
import kotlinx.android.synthetic.main.activity_diary_list.*
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    internal
    lateinit var mReporter: Reporter

    @Inject
    lateinit var mLockHelper: LockHelper

    private lateinit var mCurrentFragment: Fragment


    private val KEY_HOME_PAGE = "home_page"
    private val PAGE_DIARY = 0
    private val PAGE_METER_COUNTER = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Colorful().apply(this, override = true, baseTheme = BaseTheme.THEME_APPCOMPAT)

        setContentView(R.layout.activity_diary_list)

        setupToolbar()
        setupNavigationDrawer()

        defaultSharedPreferences.getInt(KEY_HOME_PAGE, 0).apply {
            when (this) {
                PAGE_DIARY -> showDiaryListFragment()
                PAGE_METER_COUNTER -> showMeterCounterFragment()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mLockHelper.onStart(this)
    }

    override fun onResume() {
        super.onResume()
        nav_view.setCheckedItem(mCurrentItemRes)
    }

    override fun onStop() {
        super.onStop()
        mLockHelper.onStop()
    }

    override fun onBackPressed() {
        if ((mCurrentFragment as BackPressProcessor).onBackPress()) {
            return
        } else {
            super.onBackPressed()
        }
    }

    private fun setupNavigationDrawer() {
        drawer_layout.setStatusBarBackground(R.color.colorPrimaryDark)
        drawer_layout.setMDrawerListener()
        nav_view.setMItemClickListener()
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

    private fun showDiaryStatistics() {
        showFragment<StatisticsFragment>()
    }

    private fun showDiaryListFragment() {
        showFragment<DiaryListFragment>()
        defaultSharedPreferences.edit {
            putInt(KEY_HOME_PAGE, PAGE_DIARY)
        }
    }

    private fun showMeterCounterFragment() {
        showFragment<MeterCounterFragment>()
        defaultSharedPreferences.edit {
            putInt(KEY_HOME_PAGE, PAGE_METER_COUNTER)
        }
    }

    private inline fun  <reified T:Fragment> showFragment() {
        mCurrentFragment = supportFragmentManager
                .findFragmentById(R.id.contentFrame) as? T
                ?: T::class.java.newInstance().apply {
            ActivityUtils.replaceFragment(
                    supportFragmentManager, this as Fragment, R.id.contentFrame)
        }
    }

    private fun androidx.drawerlayout.widget.DrawerLayout.setMDrawerListener() {
        addDrawerListener(object : androidx.drawerlayout.widget.DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerClosed(drawerView: View) {
                mReporter.reportDrawerClosed()
            }

            override fun onDrawerOpened(drawerView: View) {
                mReporter.reportDrawerOpened()
            }

        })
    }

    private var mCurrentItemRes: Int = R.id.navigation_menu_item_diary

    private fun NavigationView.setMItemClickListener() {
        setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_menu_item_diary -> {
                    mCurrentItemRes = R.id.navigation_menu_item_diary

                    showDiaryListFragment()
                    mReporter.reportClick("main_menu_list")
                }
                R.id.navigation_menu_item_timer -> {
                    mCurrentItemRes = R.id.navigation_menu_item_timer

                    showMeterCounterFragment()
                    mReporter.reportClick("main_menu_timer")
                }
                R.id.navigation_menu_item_statistics -> {
                    mCurrentItemRes = R.id.navigation_menu_item_statistics

                    showDiaryStatistics()
                    mReporter.reportClick("main_menu_statistics")
                }
                R.id.settings_navigation_menu_item -> {
                    startActivity<SettingsActivity>()
                    mReporter.reportClick("main_menu_settings")
                }
                R.id.rate_navigation_menu_item -> {
                    try {
                        startActivity(rateApp(context))
                    } catch (e: ActivityNotFoundException) {
                        toast(R.string.error_no_google_play).show()
                    }
                    mReporter.reportClick("main_menu_rate")
                }
                R.id.share_navigation_menu_item -> {
                    startActivity(shareApp(context))
                    mReporter.reportClick("main_menu_rate")

                }
                R.id.translate_navigation_menu_item -> {
                    startActivity(helpTranslate(context))
                    mReporter.reportClick("main_menu_translate")

                }
                R.id.feedback_navigation_menu_item -> {
                    startActivity(sendFeedback(context))
                    mReporter.reportClick("main_menu_feedback")

                }
                R.id.about_navigation_menu_item -> {
                    startActivity(intentFor<AboutActivity>())
                    mReporter.reportClick("main_menu_about")

                }
                else -> {
                }
            }

            drawer_layout.closeDrawers()
            true
        }
    }

    companion object {
        const val REQUEST_ADD_DIARY = 1
    }
}

interface BackPressProcessor {
    fun onBackPress(): Boolean {
        return false
    }
}



