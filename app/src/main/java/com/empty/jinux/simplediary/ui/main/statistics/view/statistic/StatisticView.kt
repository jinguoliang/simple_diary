package com.empty.jinux.simplediary.ui.main.statistics.view.statistic

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.LayoutInflater
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.util.dayTime
import com.empty.jinux.simplediary.util.toCalendar
import com.github.mikephil.charting.data.*
import kotlinx.android.synthetic.main.layout_statistic_chart.view.*
import java.util.*

class StatisticView
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defAttr: Int = 0) : CardView(context, attrs, defAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_statistic_chart, this)


    }

    fun setDiaries(data: List<Diary>) {
        val entries = data.groupBy { it.diaryContent.displayTime.dayTime() }.map {
            BarEntry(it.key.toCalendar().get(Calendar.DAY_OF_YEAR).toFloat(),
                    it.value.fold(0, {s, c -> s+c.diaryContent.content.length}).toFloat())
        }
        val dataSet = BarDataSet(entries, "")
        dataSet.color = Color.CYAN
        dataSet.setValueTextColors(listOf(Color.BLACK, Color.RED))
        dataSet.stackLabels = arrayOf("haha", "what")

        val data = BarData(dataSet)
        statisticChat.data = data
        statisticChat.invalidate()
    }
}


