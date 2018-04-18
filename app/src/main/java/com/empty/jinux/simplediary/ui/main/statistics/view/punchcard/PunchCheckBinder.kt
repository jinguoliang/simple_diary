package com.empty.jinux.simplediary.ui.main.statistics.view.punchcard

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.empty.jinux.simplediary.R
import me.drakeet.multitype.ItemViewBinder

class PunchCheckBinder : ItemViewBinder<PunchCheckItem, PunchCheckBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.layout_punchard_check_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: PunchCheckItem) {
        holder.check.apply {
            isChecked = item.checked
        }
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val check = view.findViewById<CheckBox>(R.id.punchCardCheckbox)
    }
}