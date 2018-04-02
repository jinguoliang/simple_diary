package com.empty.jinux.simplediary.ui.main.diarylist.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.util.formatCreatedTime

class DiaryViewHolder
internal constructor(
        v: View
) : RecyclerView.ViewHolder(v) {

    private var titleTV: TextView = v.findViewById(R.id.title)
    private var createTimeTv: TextView = v.findViewById(R.id.createTime)

    fun bind(diary: Diary, mItemListener: DiariesRecyclerViewWithCategoriesAdapter.DiaryItemListener) {
        titleTV.text = diary.diaryContent.content
        createTimeTv.text = diary.formatCreatedTime()
        itemView.setOnClickListener { mItemListener.onClick(diary) }
    }

}