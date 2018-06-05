package com.empty.jinux.simplediary.ui.main.diarylist.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.empty.jinux.simplediary.R

class CategoryViewHolder
internal constructor(v: View) : RecyclerView.ViewHolder(v) {

    private var weekth: TextView = v.findViewById(R.id.weekth)
    private var year: TextView = v.findViewById(R.id.year)

    fun bind(weekstr: String, yearStr: String) {
        weekth.text = weekstr
        year.text = yearStr
    }

}

class CategoryEndViewHolder
internal constructor(v: View) : RecyclerView.ViewHolder(v)