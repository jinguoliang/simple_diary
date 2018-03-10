package com.empty.jinux.simplediary.ui.main.diarylist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.ui.main.DiaryViewHolder
import com.google.common.base.Preconditions
import com.google.common.collect.Lists

internal class DiariesAdapter(
        diaries: List<Diary>,
        private val mItemListener: DiaryItemListener
) : RecyclerView.Adapter<DiaryViewHolder>() {

    private var mDiaries: List<Diary> = Lists.newArrayList()

    init {
        setList(diaries)
    }

    fun replaceData(diaries: List<Diary>) {
        setList(diaries)
        notifyDataSetChanged()
    }

    private fun setList(diaries: List<Diary>) {
        mDiaries = Preconditions.checkNotNull(diaries)
    }

    override fun getItemCount(): Int {
        return mDiaries.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        return DiaryViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.recycle_item_diary, parent, false), mItemListener)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        holder.bind(mDiaries[position])
    }

    interface DiaryItemListener {

        fun onClick(diary: Diary)

        fun onCompleteClick(diary: Diary)

        fun onActivateClick(diary: Diary)
    }
}