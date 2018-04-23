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
import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.ui.main.statistics.view.punchcard.PunchCheckItem
import com.empty.jinux.simplediary.ui.main.statistics.view.punchcard.PunchCheckState
import com.empty.jinux.simplediary.util.dayTime
import com.empty.jinux.simplediary.util.rangeTo
import com.empty.jinux.simplediary.util.toCalendar
import com.empty.jinux.simplediary.util.today
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.statistics_frag.*
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
        val days = diaries
                .filter {
                    val t = it.diaryContent.displayTime.dayTime().toCalendar()
                    ((t.before(today()) or (t == today()))
                            and (t.add(Calendar.DAY_OF_MONTH, 100)
                            .run { t.after(today()) }.apply { t.add(Calendar.DAY_OF_MONTH, -100) }))
                }.groupBy { it.diaryContent.displayTime.dayTime().toCalendar() }
        punchCard.setWordCountOfEveryday(days.mapWithState())
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

private fun Map<Calendar, List<Diary>>.mapWithState(): List<PunchCheckItem> {
    if (isEmpty()) {
        return emptyList()
    }

    val first = keys.first()
    return (first..today()).map {
        PunchCheckItem(it, if (get(it)?.fold(0) { s, c -> s + c.diaryContent.content.length } ?: 0 > 25) {
            PunchCheckState.STATE_CHECKED
        } else {
            if (it == today()) {
                PunchCheckState.STATE_NEED_CHECKED
            } else {
                PunchCheckState.STATE_MISSED
            }
        })
    }
}
