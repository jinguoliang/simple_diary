package com.empty.jinux.simplediary.ui.main.diarylist.adapter

import com.droidcba.kedditbysteps.commons.adapter.ViewType


val VIEW_TYPE_CATEGORY = 1

class CategoryItem(val time: Long) : ViewType{
    override fun getViewType(): Int {
        return VIEW_TYPE_CATEGORY
    }
}