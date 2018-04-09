package com.empty.jinux.simplediary.ui.main.diarylist.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.util.formatToTime
import com.empty.jinux.simplediary.util.formatToWeekday
import com.empty.jinux.simplediary.util.getScreenWidth
import org.jetbrains.anko.dimen

class DiaryViewHolder
internal constructor(
        v: View,
        private val mItemListener: DiariesRecyclerViewWithCategoriesAdapter.DiaryItemListener
) : ItemWithSwipeSettingsViewHolder(v) {

    private val titleTV: TextView = v.findViewById(R.id.title)
    private val weekName: TextView = v.findViewById(R.id.weekName)
    private val time: TextView = v.findViewById(R.id.time)

    private lateinit var currentDiary: Diary


    fun bind(diary: Diary) {
        closeMenu()

        titleTV.text = diary.diaryContent.getTitleFromContent().takeIf { it.isNotEmpty() }
                ?: itemView.resources.getString(R.string.untitled)
        weekName.text = diary.diaryContent.displayTime.formatToWeekday()
        time.text = diary.diaryContent.displayTime.formatToTime()

        currentDiary = diary

    }

    fun showWeekday(differentDay: Boolean) {
        weekName.visibility = if (differentDay) View.VISIBLE else View.INVISIBLE
    }

    override fun onDeleteClick() {
        mItemListener.onDeleteClick(currentDiary)
    }

    override fun onItemClick() {
        mItemListener.onClick(currentDiary)
    }

}

abstract class ItemWithSwipeSettingsViewHolder(v: View) : RecyclerView.ViewHolder(SwipeView(v)) {
    init {
        itemView as SwipeView
        v.setOnClickListener {
            if (itemView.isOpen()) {
                itemView.close()
            } else {
                onItemClick()
            }
        }
        itemView.findViewById<TextView>(R.id.swipe_settings_delete)
                .setOnClickListener {
                    onDeleteClick()
                }
    }

    protected fun closeMenu() {
        itemView as SwipeView
        itemView.close()
    }

    protected abstract fun onItemClick()

    protected abstract fun onDeleteClick()
}

class SwipeView(v: View) : HorizontalScrollView(v.context) {
    val swipeMenuWidth: Int

    init {
        scrollBarSize = 0
        overScrollMode = View.OVER_SCROLL_NEVER

        val linearLayout = LinearLayout(context)
        linearLayout.addView(v, LinearLayout.LayoutParams(context.getScreenWidth(),
                context.dimen(R.dimen.diary_list_item_height)))
        val menuLayout = inflateMenuLayout()
        swipeMenuWidth = context.dimen(R.dimen.swipe_item_menu_item_width)

        linearLayout.addView(menuLayout)
        addView(linearLayout)
    }

    private fun inflateMenuLayout(): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_swipe_item_settings, this, false)
    }

    fun isOpen(): Boolean {
        return scrollX > 0
    }

    fun close() {
        smoothScrollTo(0, scrollY)
    }

    fun open() {
        smoothScrollTo(swipeMenuWidth, scrollY)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_UP) {
            if (scrollX < swipeMenuWidth / 2) {
                close()
            } else {
                open()
            }
        }
        return super.onTouchEvent(ev)
    }

}
