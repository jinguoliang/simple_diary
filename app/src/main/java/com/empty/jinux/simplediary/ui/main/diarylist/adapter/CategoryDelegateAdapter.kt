package com.empty.jinux.simplediary.ui.main.diarylist.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.droidcba.kedditbysteps.commons.adapter.ViewType
import com.droidcba.kedditbysteps.commons.adapter.ViewTypeDelegateAdapter
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.util.formatToWeekOfYear
import com.empty.jinux.simplediary.util.toCalendar
import java.util.*

class CategoryDelegateAdapter : ViewTypeDelegateAdapter {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return CategoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item_category, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as CategoryViewHolder
        item as CategoryItem
        holder.bind(item.time.formatToWeekOfYear(),
                "" + item.time.toCalendar().get(Calendar.YEAR))
    }
}

class CategoryEndDelegateAdapter : ViewTypeDelegateAdapter {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return CategoryEndViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item_category_end, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
    }
}
