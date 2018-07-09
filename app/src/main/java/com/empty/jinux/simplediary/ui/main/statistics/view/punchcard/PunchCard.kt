package com.empty.jinux.simplediary.ui.main.statistics.view.punchcard

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.empty.jinux.baselibaray.thread.ThreadPools
import com.empty.jinux.baselibaray.utils.*
import com.empty.jinux.baselibaray.view.recycleview.Item
import com.empty.jinux.baselibaray.view.recycleview.ItemController
import com.empty.jinux.baselibaray.view.recycleview.replaceData
import com.empty.jinux.baselibaray.view.recycleview.withItems
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.STREAK_MIN_WORDS_COUNTS
import com.empty.jinux.simplediary.data.Diary
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.layout_punchard_check_item.*
import kotlinx.android.synthetic.main.layout_punchcard.view.*
import java.util.*

class PunchCard @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private val items = mutableListOf<Item>()

    fun setWordCountOfEveryday(diaries: List<Diary>) {
        ThreadPools.postOnQuene {
            val items = past100day(diaries).mapWithState()
            ThreadPools.postOnUI {
                punchRecycleView.replaceData(items)
                ThreadPools.postOnUIDelayed(300) {
                    punchRecycleView.smoothScrollToPosition(items.size)
                }
                val (current, longest) = computeLongestPunch(items)
                currentPunch.text = context.getString(R.string.current_punch_fmt, current)
                longestPunch.text = context.getString(R.string.longest_punch_fmt, longest)
            }
        }
    }

    private fun past100day(diaries: List<Diary>) = diaries
            .filter { diary ->
                diary.day() in (today().apply { add(Calendar.DAY_OF_YEAR, -100) })..today()
            }.groupBy { it.day() }

    private fun computeLongestPunch(counts: List<PunchCheck>): List<Int> {
        var longest = 0
        var currentPunch = 0
        counts.forEach {
            if (it.state == PunchCheckState.STATE_CHECKED) {
                currentPunch++
            } else if (it.state == PunchCheckState.STATE_MISSED) {
                longest = Math.max(longest, currentPunch)
                currentPunch = 0
            }
        }
        longest = Math.max(longest, currentPunch)
        return listOf(currentPunch, longest)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_punchcard, this)
        punchRecycleView.withItems(items)
    }

    fun setTitle(title: String) {
        streakCardTitle.text = title
    }

}

enum class PunchCheckState {
    STATE_CHECKED, STATE_MISSED, STATE_NEED_CHECKED
}

private class PunchCheck(val data: Calendar,
                         val state: PunchCheckState) : Item {
    override val controller: ItemController
        get() = Controller

    private companion object Controller : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_punchard_check_item, parent, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as PunchCheck
            when (item.state) {
                PunchCheckState.STATE_CHECKED -> {
                    holder.stateChecked.visibility = View.VISIBLE
                    holder.stateMissed.visibility = View.INVISIBLE
                }
                PunchCheckState.STATE_MISSED -> {
                    holder.stateChecked.visibility = View.INVISIBLE
                    holder.stateMissed.visibility = View.VISIBLE
                }
                PunchCheckState.STATE_NEED_CHECKED -> {
                    holder.stateChecked.visibility = View.INVISIBLE
                    holder.stateMissed.visibility = View.INVISIBLE
                }
            }
            holder.weekName.text = item.data.timeInMillis.formatToWeekday()
        }
    }

    private class ViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer
}

private fun Diary.day() =
        diaryContent.displayTime.dayTime().toCalendar()

private fun Map<Calendar, List<Diary>>.mapWithState(): List<PunchCheck> {
    if (isEmpty()) {
        return emptyList()
    }

    val first = keys.first()
    return (first..today()).map { day ->
        PunchCheck(day, when {
            checkSatisfyForPunch(day) -> PunchCheckState.STATE_CHECKED
            day == today() -> PunchCheckState.STATE_NEED_CHECKED
            else -> PunchCheckState.STATE_MISSED
        })
    }
}

private fun Map<Calendar, List<Diary>>.checkSatisfyForPunch(day: Calendar): Boolean {
    val wordsCount = get(day)?.fold(0) { s, c -> s + c.diaryContent.content.wordsCount() } ?: 0
    return wordsCount > STREAK_MIN_WORDS_COUNTS
}