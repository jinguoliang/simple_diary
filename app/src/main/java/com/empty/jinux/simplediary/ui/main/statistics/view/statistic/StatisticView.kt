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
import com.empty.jinux.simplediary.util.toCalendar
import com.empty.jinux.simplediary.util.wordsCount
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.android.synthetic.main.layout_statistic_chart.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

class StatisticView
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defAttr: Int = 0) : CardView(context, attrs, defAttr) {

    val CALENDAR_FEILDS = listOf(Calendar.DAY_OF_YEAR,
            Calendar.WEEK_OF_YEAR,
            Calendar.MONTH,
            Calendar.YEAR)

    private var currentXAxis: Int = 0

    private var currentYAxis: Int = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_statistic_chart, this)

        yAxis.adapter = ArrayAdapter<String>(context,
                R.layout.statistics_card_spinner_item,
                context.resources.getStringArray(R.array.statistics_yaxis_select_values)).apply {
            setDropDownViewResource(R.layout.statistics_card_spinner_drop_down_item)
        }
        yAxis.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentYAxis = position
                setDiaries(mDiaries)
            }

        }

        xAxis.adapter = ArrayAdapter<String>(context,
                R.layout.statistics_card_spinner_item,
                context.resources.getStringArray(R.array.statistics_xaxis_select_values)).apply {
            setDropDownViewResource(R.layout.statistics_card_spinner_drop_down_item)
        }

        xAxis.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentXAxis = CALENDAR_FEILDS[position]
                setDiaries(mDiaries)
            }
        }

        initBarChart()
    }

    private lateinit var mDiaries: List<Diary>

    fun setDiaries(diaries: List<Diary>) {
        mDiaries = diaries
        doAsync {
            val entries = diaries.groupBy { it.diaryContent.displayTime.toCalendar().get(currentXAxis) }.map {
                BarEntry((it.key).toFloat(), getYAxisData(it.value))
            }

            val dataSet = BarDataSet(entries, "").apply {
                color = ContextCompat.getColor(context, R.color.colorAccent)
                form = Legend.LegendForm.NONE
                barBorderWidth = 1f
                barBorderColor = ContextCompat.getColor(context, android.R.color.white)
                isHighlightEnabled = false
            }

            val data = BarData(dataSet).apply {
                setDrawValues(true)
                setValueTextColor(ContextCompat.getColor(context, android.R.color.white))
                setValueTextSize(15f)
                setValueFormatter { value, _, _, _ ->
                    value.toLong().toString()
                }
            }
            uiThread {
                statisticChat.xAxis.setValueFormatter { value, _ ->
                    value.toInt().toString()
                }
                statisticChat.viewPortHandler.let {
                    val xScale = entries.size / 7f
                    it.setMinMaxScaleX(xScale, xScale)
                    it.setMinMaxScaleY(1f, 1f)
                }
                statisticChat.data = data
                statisticChat.invalidate()
            }
        }


    }

    private fun getYAxisData(value: List<Diary>): Float {
        if (currentYAxis == 0) {
            return value.fold(0, { s, c -> s + c.diaryContent.content.wordsCount() }).toFloat()
        } else {
            return value.size.toFloat()
        }
    }

    private fun initBarChart() {
        statisticChat.setDrawGridBackground(false)
        statisticChat.setDrawBorders(false)

        val axisColor = Color.WHITE
        val axisWidth = 1f

        statisticChat.xAxis.apply {
            isEnabled = true
            position = XAxis.XAxisPosition.BOTTOM
            textColor = ContextCompat.getColor(context, android.R.color.white)
            textSize = 12f
            setDrawGridLines(false)
            setDrawLabels(true)
            axisLineColor = axisColor
            axisLineWidth = axisWidth
            granularity = 1f
            labelCount = 7
        }

        statisticChat.axisRight.isEnabled = false

        statisticChat.axisLeft.apply {
            setDrawGridLines(false)
            setDrawLabels(false)
            axisLineColor = axisColor
            axisLineWidth = axisWidth
        }

        statisticChat.setFitBars(false)

        statisticChat.description.isEnabled = false
        statisticChat.extraBottomOffset = 20f



    }
}




