package com.empty.jinux.simplediary.ui.main.diarylist.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.util.formatToTime
import com.empty.jinux.simplediary.util.formatToWeekday

class DiaryViewHolder
internal constructor(
        v: View
) : RecyclerView.ViewHolder(v) {

    private var titleTV: TextView = v.findViewById(R.id.title)
    private var weekName: TextView = v.findViewById(R.id.weekName)
    private var time: TextView = v.findViewById(R.id.time)

    fun bind(diary: Diary, mItemListener: DiariesRecyclerViewWithCategoriesAdapter.DiaryItemListener) {
        titleTV.text = diary.diaryContent.content
        weekName.text = diary.diaryContent.displayTime.formatToWeekday()
        time.text = diary.diaryContent.displayTime.formatToTime()
        itemView.setOnClickListener { mItemListener.onClick(diary) }
    }

}