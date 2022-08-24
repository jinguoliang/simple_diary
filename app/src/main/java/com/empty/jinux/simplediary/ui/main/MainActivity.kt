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
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.empty.jinux.baselibaray.utils.startActivity
import com.empty.jinux.baselibaray.utils.toast
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.databinding.ActivityDiaryListBinding
import com.empty.jinux.simplediary.intent.helpTranslate
import com.empty.jinux.simplediary.intent.rateApp
import com.empty.jinux.simplediary.intent.sendFeedback
import com.empty.jinux.simplediary.intent.shareApp
import com.empty.jinux.simplediary.report.Reporter
import com.empty.jinux.simplediary.ui.LockHelper
import com.empty.jinux.simplediary.ui.about.AboutActivity
import com.empty.jinux.simplediary.ui.main.diarylist.DiaryListFragment
import com.empty.jinux.simplediary.ui.main.statistics.StatisticsFragment
import com.empty.jinux.simplediary.ui.settings.SettingsActivity
import com.empty.jinux.simplediary.util.ActivityUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    internal
    lateinit var mReporter: Reporter

    @Inject
    lateinit var mLockHelper: LockHelper

    private lateinit var mCurrentFragment: BackPressPrecessor
    lateinit var binding: ActivityDiaryListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setupNavigationDrawer()
        showDiaryListFragment()
    }

    override fun onStart() {
        super.onStart()
        mLockHelper.onStart(this)
    }

    override fun onResume() {
        super.onResume()
        binding.navView.setCheckedItem(mCurrentItemRes)
    }

    override fun onStop() {
        super.onStop()
        mLockHelper.onStop()
    }

    override fun onBackPressed() {
        if (mCurrentFragment.onBackPress()) {
            return
        } else {
            super.onBackPressed()
        }
    }

    private fun setupNavigationDrawer() {
        binding.drawerLayout.setStatusBarBackground(R.color.colorPrimaryDark)
        binding.drawerLayout.setMDrawerListener()
        binding.navView.setMItemClickListener()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        val ab = supportActionBar
        ab!!.setHomeAsUpIndicator(R.drawable.ic_menu)
        ab.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                binding.drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDiaryStatistics() {
        val fragment =
            supportFragmentManager.findFragmentById(R.id.contentFrame) as? StatisticsFragment
                ?: StatisticsFragment.newInstance()
        ActivityUtils.replaceFragment(
            supportFragmentManager, fragment, R.id.contentFrame
        )
        mCurrentFragment = fragment
    }

    private fun showDiaryListFragment() {
        val fragment =
            supportFragmentManager.findFragmentById(R.id.contentFrame) as? DiaryListFragment
                ?: DiaryListFragment.newInstance()
        ActivityUtils.replaceFragment(
            supportFragmentManager, fragment, R.id.contentFrame
        )
        mCurrentFragment = fragment
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

    private var mCurrentItemRes: Int = R.id.list_navigation_menu_item

    private fun com.google.android.material.navigation.NavigationView.setMItemClickListener() {
        setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.list_navigation_menu_item -> {
                    mCurrentItemRes = R.id.list_navigation_menu_item

                    showDiaryListFragment()
                    mReporter.reportClick("main_menu_list")
                }
                R.id.statistics_navigation_menu_item -> {
                    mCurrentItemRes = R.id.statistics_navigation_menu_item

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
                        toast(R.string.error_no_google_play)
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
                    startActivity<AboutActivity>()
                    mReporter.reportClick("main_menu_about")

                }
                else -> {
                }
            }

            binding.drawerLayout.closeDrawers()
            true
        }
    }

    companion object {
        const val REQUEST_ADD_DIARY = 1
    }
}

interface BackPressPrecessor {
    fun onBackPress(): Boolean {
        return false
    }
}



