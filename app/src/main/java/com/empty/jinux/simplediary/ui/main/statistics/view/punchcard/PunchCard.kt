package com.empty.jinux.simplediary.ui.main.statistics.view.punchcard

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.empty.jinux.baselibaray.thread.ThreadPools
import com.empty.jinux.baselibaray.view.recycleview.Item
import com.empty.jinux.baselibaray.view.recycleview.ItemController
import com.empty.jinux.baselibaray.view.recycleview.withItems
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.util.formatToWeekday
import kotlinx.android.synthetic.main.layout_punchcard.view.*
import java.util.*

class PunchCard @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    fun setWordCountOfEveryday(counts: List<PunchCheckItem>) {
        punchRecycleView.withItems(counts.map { PunchCheck(it.data as Calendar, it.state) })
        ThreadPools.postOnUI {
            punchRecycleView.smoothScrollToPosition(counts.size)
        }


        val (current, longest) = computeLongestPunch(counts)
        currentPunch.text = context.getString(R.string.current_punch_fmt, current)
        longestPunch.text = context.getString(R.string.longest_punch_fmt, longest)
    }

    private fun computeLongestPunch(counts: List<PunchCheckItem>): List<Int> {
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
        punchRecycleView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    fun setTitle(title: String) {
        streakCardTitle.text = title
    }

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
                        holder.checked.visibility = View.VISIBLE
                        holder.missed.visibility = View.INVISIBLE
                    }
                    PunchCheckState.STATE_MISSED -> {
                        holder.checked.visibility = View.INVISIBLE
                        holder.missed.visibility = View.VISIBLE
                    }
                    PunchCheckState.STATE_NEED_CHECKED -> {
                        holder.checked.visibility = View.INVISIBLE
                        holder.missed.visibility = View.INVISIBLE
                    }
                }
                holder.weekName.text = item.data.timeInMillis.formatToWeekday()
            }
        }

    private class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checked = view.findViewById<View>(R.id.stateChecked)!!
        val missed = view.findViewById<View>(R.id.stateMissed)!!
        val weekName = view.findViewById<TextView>(R.id.weekName)!!
    }
}