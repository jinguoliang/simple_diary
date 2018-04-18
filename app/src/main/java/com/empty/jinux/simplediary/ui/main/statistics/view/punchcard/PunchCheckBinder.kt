package com.empty.jinux.simplediary.ui.main.statistics.view.punchcard

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.util.formatToWeekday
import me.drakeet.multitype.ItemViewBinder
import java.util.*

class PunchCheckBinder : ItemViewBinder<PunchCheckItem, PunchCheckBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.layout_punchard_check_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: PunchCheckItem) {
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
        item.data as Calendar
        holder.weekName.text = item.data.timeInMillis.formatToWeekday()
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checked = view.findViewById<View>(R.id.stateChecked)!!
        val missed = view.findViewById<View>(R.id.stateMissed)!!
        val weekName = view.findViewById<TextView>(R.id.weekName)!!
    }
}