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
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import kotlinx.android.synthetic.main.layout_statistic_chart.view.*
import kotlinx.android.synthetic.main.recycler_view_item_category.view.*
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
            Entry(it.key.toCalendar().get(Calendar.DAY_OF_YEAR).toFloat(),
                    it.value.fold(0, {s, c -> s+c.diaryContent.content.length}).toFloat())
        }
        val dataSet = LineDataSet(entries, "")
        dataSet.color = Color.RED
        dataSet.form = Legend.LegendForm.NONE
        dataSet.lineWidth = 5f
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.isHighlightEnabled = false
        dataSet.setValueTextColors(listOf(Color.BLACK))

        statisticChat.data = LineData(dataSet).apply {
            setDrawValues(false)
        }
        statisticChat.invalidate()
        statisticChat.setVisibleXRangeMaximum(7f)
        statisticChat.setDrawGridBackground(false)
        statisticChat.setDrawBorders(false)
        val xAxis = statisticChat.xAxis
        xAxis.isEnabled = true
        xAxis.isDrawAxisLineEnabled
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawLabels(false)

        statisticChat.axisRight.isEnabled = false
        statisticChat.axisLeft.apply {
            setDrawGridLines(false)
            setDrawLabels(false)
        }

        statisticChat.description.isEnabled = false
    }
}


