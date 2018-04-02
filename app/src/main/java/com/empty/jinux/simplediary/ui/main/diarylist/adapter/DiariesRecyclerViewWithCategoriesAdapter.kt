package com.empty.jinux.simplediary.ui.main.diarylist.adapter

import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.ViewGroup
import com.droidcba.kedditbysteps.commons.adapter.ViewType
import com.droidcba.kedditbysteps.commons.adapter.ViewTypeDelegateAdapter
import com.empty.jinux.simplediary.data.Diary
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

        var preDate = 0L
        diaries.forEach {
            if (it.meta.createdTime.weekStartTime() != preDate) {
                preDate = it.meta.createdTime.weekStartTime()
                items.add(CategoryItem(preDate))
            }
            items.add(DiaryItem(it))
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

