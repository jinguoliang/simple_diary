package com.empty.jinux.simplediary.ui.main.statistics.view.statistic

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.LayoutInflater
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.Diary
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.layout_statistic_chart.view.*

class StatisticView
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defAttr: Int = 0) : CardView(context, attrs, defAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_statistic_chart, this)


    }

    fun setDiaries(data: List<Diary>) {
        val data = listOf(1, 2, 4, 5, 6)
        val entries = data.map {
            val f = it.toFloat()
            Entry(f, f)
        }
        val dataSet = LineDataSet(entries, "Label1")
        dataSet.setColor(Color.CYAN)
        dataSet.setValueTextColors(listOf(Color.BLACK, Color.RED))

        val lineData = LineData(dataSet)
        statisticChat.data = lineData
        statisticChat.invalidate()
    }
}