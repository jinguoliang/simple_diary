package com.empty.jinux.simplediary.ui.diarylist

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.util.formatCreatedTime

class DiaryViewHolder
internal constructor(
        v: View,
        val mItemListener: DiariesAdapter.DiaryItemListener
) : RecyclerView.ViewHolder(v) {

    private var titleTV: TextView = v.findViewById(R.id.title)
    private var completeCB: CheckBox = v.findViewById(R.id.complete)
    private var createTimeTv: TextView = v.findViewById(R.id.createTime)

    fun bind(diary: Diary): Unit {
        titleTV.text = diary.titleForList
        // Active/completed diary UI
        completeCB.isChecked = diary.isCompleted
        createTimeTv.text = diary.formatCreatedTime()

        if (diary.isCompleted) {
            itemView.setBackgroundDrawable(itemView.context
                    .resources.getDrawable(R.drawable.list_completed_touch_feedback))
        } else {
            itemView.setBackgroundDrawable(itemView.context
                    .resources.getDrawable(R.drawable.touch_feedback))
        }

        completeCB.setOnClickListener {
            if (!diary.isCompleted) {
                mItemListener.onCompleteClick(diary)
            } else {
                mItemListener.onActivateClick(diary)
            }
        }

        itemView.setOnClickListener { mItemListener.onClick(diary) }
    }

}