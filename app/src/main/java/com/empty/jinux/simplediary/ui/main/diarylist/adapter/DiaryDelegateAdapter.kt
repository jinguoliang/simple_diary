package com.empty.jinux.simplediary.ui.main.diarylist.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.droidcba.kedditbysteps.commons.adapter.ViewType
import com.droidcba.kedditbysteps.commons.adapter.ViewTypeDelegateAdapter
import com.empty.jinux.simplediary.R

class DiaryDelegateAdapter internal constructor(private val mItemListener: DiariesRecyclerViewWithCategoriesAdapter.DiaryItemListener) : ViewTypeDelegateAdapter {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return DiaryViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.recycle_item_diary, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
        val holder = holder as DiaryViewHolder
        item as DiaryItem
        holder.bind(item.data, mItemListener)
        holder.showWeekday(item.differentDay)
    }
}