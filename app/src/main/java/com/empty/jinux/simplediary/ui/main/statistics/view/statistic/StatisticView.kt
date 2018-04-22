package com.empty.jinux.simplediary.ui.main.statistics.view.statistic

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.util.dayTime
import com.empty.jinux.simplediary.util.toCalendar
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.android.synthetic.main.layout_statistic_chart.view.*
import java.util.*

class StatisticView
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defAttr: Int = 0) : CardView(context, attrs, defAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_statistic_chart, this)

        yAxis.adapter = ArrayAdapter<String>(context,
                R.layout.statistics_card_spinner_item,
                arrayOf("Words", "Articles")).apply {
            setDropDownViewResource(R.layout.statistics_card_spinner_drop_down_item)
        }
//        yAxis.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//        }

        xAxis.adapter = ArrayAdapter<String>(context,
                R.layout.statistics_card_spinner_item,
                arrayOf("Day", "Week", "Month", "Year")).apply {
            setDropDownViewResource(R.layout.statistics_card_spinner_drop_down_item)
        }
    }

    fun setDiaries(data: List<Diary>) {
        val entries = data.groupBy { it.diaryContent.displayTime.dayTime() }.map {
            BarEntry((it.key.toCalendar().get(Calendar.DAY_OF_YEAR)).toFloat(),
                    it.value.fold(0, { s, c -> s + c.diaryContent.content.length }).toFloat())
        }
        val dataSet = BarDataSet(entries, "")
        dataSet.color = Color.RED
        dataSet.form = Legend.LegendForm.NONE
        dataSet.barBorderWidth = 3f
//        dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        dataSet.isHighlightEnabled = false
        statisticChat.data = BarData(dataSet).apply {
            setDrawValues(true)
            setValueTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            setValueTextSize(15f)
            setValueFormatter { value, entry, dataSetIndex, viewPortHandler ->
                value.toLong().toString()
            }
        }
        statisticChat.invalidate()
        statisticChat.setVisibleXRangeMaximum(7f)
        statisticChat.setDrawGridBackground(false)
        statisticChat.setDrawBorders(false)
        statisticChat.xAxis.apply {
            isEnabled = true
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            setDrawLabels(true)
            axisLineColor = Color.BLACK
        }

        statisticChat.axisRight.isEnabled = false

        statisticChat.axisLeft.apply {
            setDrawGridLines(false)
            setDrawLabels(false)
            axisLineColor = Color.BLACK

        }

        statisticChat.description.isEnabled = false
    }
}


