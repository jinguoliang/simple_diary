package com.empty.jinux.simplediary.ui.main.diarylist.adapter

import com.droidcba.kedditbysteps.commons.adapter.ViewType
import com.empty.jinux.simplediary.data.Diary


val VIEW_TYPE_CATEGORY = 1
val VIEW_TYPE_CATEGORY_END = 2
val VIEW_TYPE_DIARY = 3

class DiaryItem(val data: Diary, val differentDay: Boolean) : ViewType{
    override fun getViewType(): Int {
        return VIEW_TYPE_DIARY
    }
}

class CategoryItem(val time: Long) : ViewType{
    override fun getViewType(): Int {
        return VIEW_TYPE_CATEGORY
    }
}

class CategoryEndItem() : ViewType {
    override fun getViewType(): Int {
        return VIEW_TYPE_CATEGORY_END
    }
}