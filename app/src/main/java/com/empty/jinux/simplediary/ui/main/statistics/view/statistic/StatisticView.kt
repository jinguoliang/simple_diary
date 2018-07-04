package com.empty.jinux.simplediary.ui.main.statistics.view.statistic

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.empty.jinux.baselibaray.thread.ThreadPools
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.util.toCalendar
import com.empty.jinux.simplediary.util.wordsCount
import kotlinx.android.synthetic.main.layout_statistic_chart.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

class StatisticView
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defAttr: Int = 0) : CardView(context, attrs, defAttr) {

    val CALENDAR_FEILDS = listOf(Calendar.DAY_OF_MONTH,
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

    }

    private lateinit var mDiaries: List<Diary>

    fun setDiaries(diaries: List<Diary>) {
        mDiaries = diaries
        ThreadPools.postOnQuene {
            val entries = diaries.groupBy { it.diaryContent.displayTime.getBaseStart(currentXAxis) }.map {
                it.key to getYAxisData(it.value).toLong()
            }.toList()

            ThreadPools.postOnUI {
                statisticChat.setXAxisValueFormater(mFormatter)
                statisticChat.setData(entries)
            }
        }
    }

    private val mFormatter = object : BarChart.Formater {
        override fun format(v: Long): String {
            val cal = v.toCalendar()
            return when (currentXAxis) {
                Calendar.YEAR -> "${cal.get(Calendar.YEAR)}年"
                Calendar.MONTH -> "${cal.get(Calendar.MONTH)}月"
                Calendar.WEEK_OF_YEAR -> "${cal.get(Calendar.WEEK_OF_YEAR)}周"
                Calendar.DAY_OF_MONTH -> "${cal.get(Calendar.DAY_OF_MONTH)}日"
                else -> ""
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
}

private fun Long.getBaseStart(currentXAxis: Int): Long {
    val cal = toCalendar()
    cal.set(Calendar.MILLISECOND, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    when (currentXAxis) {
        Calendar.WEEK_OF_YEAR -> cal.set(Calendar.DAY_OF_WEEK, 0)
        Calendar.MONTH -> cal.set(Calendar.DAY_OF_MONTH, 0)
        Calendar.YEAR -> cal.set(Calendar.DAY_OF_YEAR, 0)
    }
    return cal.timeInMillis
}




