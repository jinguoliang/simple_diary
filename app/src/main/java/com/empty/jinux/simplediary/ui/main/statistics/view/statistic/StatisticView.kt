package com.empty.jinux.simplediary.ui.main.statistics.view.statistic

import android.content.Context
import androidx.cardview.widget.CardView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.empty.jinux.baselibaray.thread.ThreadPools
import com.empty.jinux.baselibaray.utils.toCalendar
import com.empty.jinux.baselibaray.utils.wordsCount
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.databinding.LayoutStatisticChartBinding
import com.empty.jinux.simplediary.report.Reporter
import java.util.*

class StatisticView
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defAttr: Int = 0) : androidx.cardview.widget.CardView(context, attrs, defAttr) {

    companion object {
        val CALENDAR_FIELDS = listOf(Calendar.DAY_OF_MONTH,
                Calendar.WEEK_OF_YEAR,
                Calendar.MONTH,
                Calendar.YEAR)
        val CALENDAR_FIELD_NAMES = listOf("day_of_month",
                "week_of_year",
                "month",
                "year")
        val YAXIAS_NAMES = listOf("words", "articles")
    }


    private var currentXAxis: Int = 0
    private var currentYAxis: Int = 0

    private lateinit var binding: LayoutStatisticChartBinding
    init {
        binding = LayoutStatisticChartBinding.inflate(LayoutInflater.from(context), this, true)

        binding.yAxis.adapter = ArrayAdapter<String>(context,
                R.layout.statistics_card_spinner_item,
                context.resources.getStringArray(R.array.statistics_yaxis_select_values)).apply {
            setDropDownViewResource(R.layout.statistics_card_spinner_drop_down_item)
        }
        binding.yAxis.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentYAxis = position
                mReporter.reportClick("Statistics_YAxis", YAXIAS_NAMES[position])
                setDiaries(mDiaries)
            }

        }

        binding.xAxis.adapter = ArrayAdapter<String>(context,
                R.layout.statistics_card_spinner_item,
                context.resources.getStringArray(R.array.statistics_xaxis_select_values)).apply {
            setDropDownViewResource(R.layout.statistics_card_spinner_drop_down_item)
        }

        binding.xAxis.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentXAxis = CALENDAR_FIELDS[position]
                mReporter.reportClick("Statistics_XAxis", CALENDAR_FIELD_NAMES[position])
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
                binding.statisticChat.setXAxisValueFormator(mFormatter)
                binding.statisticChat.setData(entries)
            }
        }
    }

    private val mFormatter = object : BarChart.Formator {
        override fun format(v: Long): String {
            val cal = v.toCalendar()
            return when (currentXAxis) {
                Calendar.YEAR -> context.getString(R.string.fmt_statistics_year, cal.get(Calendar.YEAR))
                Calendar.MONTH -> context.getString(R.string.fmt_statistics_month, cal.get(Calendar.MONTH))
                Calendar.WEEK_OF_YEAR -> context.getString(R.string.fmt_statistics_week, cal.get(Calendar.WEEK_OF_YEAR))
                Calendar.DAY_OF_MONTH -> context.getString(R.string.fmt_statistics_day, cal.get(Calendar.DAY_OF_MONTH))
                else -> ""
            }
        }
    }

    private fun getYAxisData(value: List<Diary>): Float {
        return if (currentYAxis == 0) {
            value.fold(0) { s, c -> s + c.diaryContent.content.wordsCount() }.toFloat()
        } else {
            value.size.toFloat()
        }
    }

    lateinit var mReporter: Reporter
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




