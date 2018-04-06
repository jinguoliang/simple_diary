package com.empty.jinux.simplediary.ui.main.diarylist.adapter

import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.ViewGroup
import com.droidcba.kedditbysteps.commons.adapter.ViewType
import com.droidcba.kedditbysteps.commons.adapter.ViewTypeDelegateAdapter
import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.util.dayTime
import com.empty.jinux.simplediary.util.weekStartTime

class DiariesRecyclerViewWithCategoriesAdapter(
        diaries: List<Diary>,
        mItemListener: DiaryItemListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var mItems: List<ViewType>
    private val mDelegateAdapters = SparseArray<ViewTypeDelegateAdapter>()

    init {
        setItems(diaries)
        mDelegateAdapters.put(VIEW_TYPE_CATEGORY, CategoryDelegateAdapter())
        mDelegateAdapters.put(VIEW_TYPE_DIARY, DiaryDelegateAdapter(mItemListener))
    }

    private fun setItems(diaries: List<Diary>) {
        val items = mutableListOf<ViewType>()

        var preWeekStart = 0L
        var preDay = 0L
        diaries.forEach {
            val createdTime = it.meta.createdTime
            if (createdTime.weekStartTime() != preWeekStart) {
                preWeekStart = createdTime.weekStartTime()
                items.add(CategoryItem(preWeekStart))
            }
            val differentDay = createdTime.dayTime() != preDay
            if (differentDay) {
                preDay = createdTime.dayTime()
            }
            items.add(DiaryItem(it, differentDay))
        }
        mItems = items
    }

    fun replaceData(diaries: List<Diary>) {
        setItems(diaries)
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return mItems[position].getViewType()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return mDelegateAdapters[viewType].onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        mDelegateAdapters[mItems[position].getViewType()].onBindViewHolder(holder, mItems[position])
    }

    interface DiaryItemListener {

        fun onClick(diary: Diary)

        fun onCompleteClick(diary: Diary)

        fun onActivateClick(diary: Diary)
    }
}


