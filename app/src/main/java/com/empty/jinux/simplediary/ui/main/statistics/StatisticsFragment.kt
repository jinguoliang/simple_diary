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

package com.empty.jinux.simplediary.ui.main.statistics

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.STREAK_MIN_WORDS_COUNTS
import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.databinding.FragmentStatisticsBinding
import com.empty.jinux.simplediary.report.Reporter
import com.empty.jinux.simplediary.ui.main.BackPressPrecessor
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * Main UI for the statistics screen.
 */
@AndroidEntryPoint
class StatisticsFragment : Fragment(), StatisticsContract.View, BackPressPrecessor {

    @Inject
    internal
    lateinit var mPresenter: StatisticsContract.Presenter

    @Inject
    lateinit var mReporter: Reporter

    private lateinit var binding: FragmentStatisticsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_add_diary)?.visibility = View.INVISIBLE
        binding.statistics.mReporter = mReporter
    }

    override fun onResume() {
        super.onResume()
        mPresenter.start()
    }

    override fun setProgressIndicator(active: Boolean) {
        binding.apply {
            if (active) {
                loadingContainer.visibility = View.VISIBLE
                statistics.visibility = View.GONE
                punchCard.visibility = View.GONE
            } else {
                loadingContainer.visibility = View.GONE
                statistics.visibility = View.VISIBLE
                punchCard.visibility = View.VISIBLE
            }
        }

    }

    override fun showStatistics(diaries: List<Diary>) {
        showPunchCard(diaries)
        showStatisticChart(diaries)
    }

    private fun showStatisticChart(diaries: List<Diary>) {
        binding.statistics.setDiaries(diaries)
    }

    private fun showPunchCard(diaries: List<Diary>) {
        binding.punchCard.setTitle(resources.getString(R.string.streak_card_title_fmt, STREAK_MIN_WORDS_COUNTS))
        binding.punchCard.setWordCountOfEveryday(diaries)
    }


    override fun showLoadingStatisticsError() {
    }

    override fun isActive(): Boolean {
        return isAdded
    }

    companion object {

        fun newInstance(): StatisticsFragment {
            return StatisticsFragment()
        }
    }
}
