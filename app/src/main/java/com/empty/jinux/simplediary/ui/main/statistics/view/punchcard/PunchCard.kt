package com.empty.jinux.simplediary.ui.main.statistics.view.punchcard

import android.content.Context
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
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
import com.empty.jinux.simplediary.databinding.LayoutPunchardCheckItemBinding
import com.empty.jinux.simplediary.databinding.LayoutPunchcardBinding
import java.util.*

class PunchCard @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : androidx.cardview.widget.CardView(context, attrs, defStyleAttr) {

    private val items = mutableListOf<Item>()

    fun setWordCountOfEveryday(diaries: List<Diary>) {
        ThreadPools.postOnQuene {
            val items = past100day(diaries).mapWithState()
            ThreadPools.postOnUI {
                binding.punchRecycleView.replaceData(items)
                ThreadPools.postOnUIDelayed(300) {
                    binding.punchRecycleView.smoothScrollToPosition(items.size)
                }
                val (current, longest) = computeLongestPunch(items)
                binding.currentPunch.text = context.getString(R.string.current_punch_fmt, current)
                binding.longestPunch.text = context.getString(R.string.longest_punch_fmt, longest)
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

    private lateinit var binding: LayoutPunchcardBinding
    init {
        binding = LayoutPunchcardBinding.inflate(LayoutInflater.from(context), this, true)
        binding.punchRecycleView.withItems(items)
    }

    fun setTitle(title: String) {
        binding.streakCardTitle.text = title
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
        private  lateinit var binding: LayoutPunchardCheckItemBinding
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            binding = LayoutPunchardCheckItemBinding.inflate(LayoutInflater.from(parent.context))
            return ViewHolder(binding.root)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            val holder = holder as ViewHolder
            val item = item as PunchCheck
            when (item.state) {
                PunchCheckState.STATE_CHECKED -> {
                    binding.stateChecked.visibility = View.VISIBLE
                    binding.stateMissed.visibility = View.INVISIBLE
                }
                PunchCheckState.STATE_MISSED -> {
                    binding.stateChecked.visibility = View.INVISIBLE
                    binding.stateMissed.visibility = View.VISIBLE
                }
                PunchCheckState.STATE_NEED_CHECKED -> {
                    binding.stateChecked.visibility = View.INVISIBLE
                    binding.stateMissed.visibility = View.INVISIBLE
                }
            }
            binding.weekName.text = item.data.timeInMillis.formatToWeekday()
        }
    }

    private class ViewHolder(containerView: View?) : RecyclerView.ViewHolder(containerView!!)
}

private fun Diary.day() =
        diaryContent.displayTime.dayStartTime().toCalendar()

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