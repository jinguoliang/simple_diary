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
import android.support.design.widget.FloatingActionButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.STREAK_MIN_WORDS_COUNTS
import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.ui.main.statistics.view.punchcard.PunchCheckItem
import com.empty.jinux.simplediary.ui.main.statistics.view.punchcard.PunchCheckState
import com.empty.jinux.simplediary.util.*
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.statistics_frag.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import javax.inject.Inject


/**
 * Main UI for the statistics screen.
 */
class StatisticsFragment : DaggerFragment(), StatisticsContract.View {

    @Inject
    internal
    lateinit var mPresenter: StatisticsPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.statistics_frag, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.findViewById<FloatingActionButton>(R.id.fab_add_diary)?.visibility = View.INVISIBLE
    }

    override fun onResume() {
        super.onResume()
        mPresenter.start()
    }

    override fun setProgressIndicator(active: Boolean) {
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

    override fun showStatistics(diaries: List<Diary>) {
        showPunchCard(diaries)
        showStatisticChart(diaries)
    }

    private fun showStatisticChart(diaries: List<Diary>) {
        statistics.setDiaries(diaries)
    }

    private fun showPunchCard(diaries: List<Diary>) {
        // todo: may be we can do it better
        doAsync {
            val days = diaries
                    .filter { diary ->
                        diary.day() in (today().apply { add(Calendar.DAY_OF_YEAR, -100) })..today()
                    }.groupBy { it.day() }
            val item = days.mapWithState()
            uiThread {
                punchCard.setWordCountOfEveryday(item)
                punchCard.setTitle(resources.getString(R.string.streak_card_title_fmt, STREAK_MIN_WORDS_COUNTS))
            }
        }
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

private fun Diary.day() =
        diaryContent.displayTime.dayTime().toCalendar()

private fun Map<Calendar, List<Diary>>.mapWithState(): List<PunchCheckItem> {
    if (isEmpty()) {
        return emptyList()
    }

    val first = keys.first()
    return (first..today()).map { day ->
        PunchCheckItem(day, if (checkSatisfyForPunch(day)) {
            PunchCheckState.STATE_CHECKED
        } else {
            if (day == today()) {
                PunchCheckState.STATE_NEED_CHECKED
            } else {
                PunchCheckState.STATE_MISSED
            }
        })
    }
}

private fun Map<Calendar, List<Diary>>.checkSatisfyForPunch(day: Calendar): Boolean {
    val wordsCount = get(day)?.fold(0) { s, c -> s + c.diaryContent.content.wordsCount() } ?: 0
    return wordsCount > STREAK_MIN_WORDS_COUNTS
}
