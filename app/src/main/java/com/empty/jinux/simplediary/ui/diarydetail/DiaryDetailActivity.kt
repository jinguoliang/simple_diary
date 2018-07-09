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

package com.empty.jinux.simplediary.ui.diarydetail

import android.os.Bundle
import com.empty.jinux.baselibaray.log.logi
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.ui.LockHelper
import com.empty.jinux.simplediary.ui.diarydetail.fragment.DiaryDetailFragment
import com.empty.jinux.simplediary.util.ActivityUtils
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_diary_detail.*
import javax.inject.Inject

/**
 * Displays task details screen.
 */
class DiaryDetailActivity : DaggerAppCompatActivity() {


    @Inject
    lateinit var mLockHelper: LockHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_diary_detail)

        setupActionBar()
        setupDetailFragment()
    }

    private fun setupDetailFragment() {
        val diaryDetailFragment = supportFragmentManager
                .findFragmentById(R.id.contentFrame) as? DiaryDetailFragment
        if (diaryDetailFragment == null) {
            ActivityUtils.addFragmentToActivity(supportFragmentManager,
                    DiaryDetailFragment.newInstance(intent.extras), R.id.contentFrame)
        } else {
            diaryDetailFragment.arguments = intent.extras
            ActivityUtils.replaceFragment(supportFragmentManager,
                    diaryDetailFragment, R.id.contentFrame)
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onStart() {
        logi("activity onStart", "detail")
        super.onStart()
        mLockHelper.onStart(this)

    }

    override fun onStop() {
        logi("activity onStop", "detail")

        super.onStop()
        mLockHelper.onStop()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        val diaryDetailFragment = supportFragmentManager
                .findFragmentById(R.id.contentFrame) as? DiaryDetailFragment
        if (diaryDetailFragment?.onBackPressed() == false) {
            super.onBackPressed()
        }
    }

    companion object {

        const val EXTRA_DIARY_ID = "TASK_ID"
        const val EXTRA_TODAY_WORD_COUNT = "TODAY_WORD_COUNT"
    }
}
