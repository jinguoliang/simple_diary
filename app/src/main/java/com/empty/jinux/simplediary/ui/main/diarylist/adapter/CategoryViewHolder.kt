package com.empty.jinux.simplediary.ui.main.diarylist.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.empty.jinux.simplediary.R

class CategoryViewHolder
internal constructor(v: View) : RecyclerView.ViewHolder(v) {

    private var labelTv: TextView = v.findViewById(R.id.label)

    fun bind(label: String) {
        labelTv.text = label
    }

}