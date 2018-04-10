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
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.INVALID_DIARY_ID
import com.empty.jinux.simplediary.ui.diarydetail.fragment.DiaryDetailFragment
import com.empty.jinux.simplediary.util.ActivityUtils
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.diary_detail_act.*

/**
 * Displays task details screen.
 */
class DiaryDetailActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.diary_detail_act)
        supportActionBar

        // Get the requested task id
        val taskId = intent.getLongExtra(EXTRA_DIARY_ID, INVALID_DIARY_ID)

        var diaryDetailFragment = supportFragmentManager
                .findFragmentById(R.id.contentFrame) as DiaryDetailFragment?

        if (diaryDetailFragment == null) {
            diaryDetailFragment = DiaryDetailFragment.newInstance(taskId)

            ActivityUtils.addFragmentToActivity(supportFragmentManager,
                    diaryDetailFragment, R.id.contentFrame)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {

        val EXTRA_DIARY_ID = "TASK_ID"
    }
}
