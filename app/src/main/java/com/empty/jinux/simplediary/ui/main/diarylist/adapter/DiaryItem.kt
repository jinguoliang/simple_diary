package com.empty.jinux.simplediary.ui.main.diarylist.adapter

import com.droidcba.kedditbysteps.commons.adapter.ViewType
import com.empty.jinux.simplediary.data.Diary


val VIEW_TYPE_DIARY = 2

class DiaryItem(val data: Diary,val differentDay: Boolean) : ViewType{
    override fun getViewType(): Int {
        return VIEW_TYPE_DIARY
    }
}