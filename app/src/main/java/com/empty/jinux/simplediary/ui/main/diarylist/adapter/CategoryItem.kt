package com.empty.jinux.simplediary.ui.main.diarylist.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.empty.jinux.baselibaray.utils.getScreenWidth
import com.empty.jinux.baselibaray.utils.inflate
import com.empty.jinux.baselibaray.view.recycleview.Item
import com.empty.jinux.baselibaray.view.recycleview.ItemController
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.source.diary.Diary
import com.empty.jinux.baselibaray.utils.formatToDay
import com.empty.jinux.baselibaray.utils.formatToTime
import com.empty.jinux.baselibaray.utils.formatToWeekday
import com.empty.jinux.baselibaray.utils.toCalendar
import kotlinx.android.synthetic.main.layout_swipe_item_settings.view.*
import kotlinx.android.synthetic.main.recycle_item_diary.view.*
import kotlinx.android.synthetic.main.recycler_view_item_category.view.*
import org.jetbrains.anko.dimen
import java.util.*

class DiaryItem(val data: Diary, val differentDay: Boolean, val onItemListener: OnItemListener) : Item {
    interface OnItemListener {
        fun onItemClick(diary: Diary)
        fun onDeleteClick(diary: Diary)
    }

    companion object Controller : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return ViewHolder(parent.inflate(R.layout.recycle_item_diary, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            item as DiaryItem
            holder as ViewHolder

            holder.bind(item.data)
            holder.showWeekday(item.differentDay)
            holder.originView.setOnClickListener {
                if (holder.isOpen()) {
                    holder.smoothClose()
                } else {
                    item.onItemListener.onItemClick(item.data)
                }
            }
            holder.itemView.swipe_settings_delete.setOnClickListener { item.onItemListener.onDeleteClick(item.data) }
        }

        class ViewHolder(val originView: View) : RecyclerView.ViewHolder(SwipeView(originView)) {
            val title = itemView.title
            val weekName: TextView = itemView.weekName
            val day: TextView = itemView.day
            val time: TextView = itemView.time
            val topLine: View = itemView.topLine

            fun bind(diary: Diary) {
                closeMenu()

                title.text = diary.diaryContent.getTitleFromContent().takeIf { it.isNotEmpty() }
                        ?: itemView.resources.getString(R.string.untitled)
                weekName.text = diary.diaryContent.displayTime.formatToWeekday()
                day.text = diary.diaryContent.displayTime.formatToDay()
                time.text = diary.diaryContent.displayTime.formatToTime()

//            currentDiary = diary

            }

            protected fun closeMenu() {
                itemView as SwipeView
                itemView.close()
            }

            fun showWeekday(differentDay: Boolean) {
                weekName.visibility = if (differentDay) View.VISIBLE else View.INVISIBLE
                day.visibility = weekName.visibility
//        topLine.visibility = weekName.visibility
                topLine.visibility = View.INVISIBLE
            }

            fun isOpen(): Boolean {
                itemView as SwipeView
                return itemView.isOpen()
            }

            fun smoothClose() {
                itemView as SwipeView
                itemView.smoothClose()
            }
        }

    }

    override val controller: ItemController = Controller

    @SuppressLint("ViewConstructor")
    class SwipeView(v: View) : HorizontalScrollView(v.context) {
        val swipeMenuWidth = context.dimen(R.dimen.swipe_item_menu_item_width)
        val diariesListHorizontalMargin = context.dimen(R.dimen.diaries_list_recycle_view_margin_horizontal)

        init {
            scrollBarSize = 0
            overScrollMode = View.OVER_SCROLL_NEVER
            isHorizontalScrollBarEnabled = false
            isHorizontalFadingEdgeEnabled = false
            layoutParams = v.layoutParams

            wrapContent(v)
        }

        private fun wrapContent(content: View) {
            val linearLayout = LinearLayout(context).apply {
                addView(content, LinearLayout.LayoutParams(context.getScreenWidth() - 2 * diariesListHorizontalMargin,
                        context.dimen(R.dimen.diary_list_item_height)))
                addView(inflateMenuLayout())
            }
            addView(linearLayout, FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            ))
        }

        private fun inflateMenuLayout(): View {
            return LayoutInflater.from(context).inflate(R.layout.layout_swipe_item_settings, this, false)
        }

        fun isOpen(): Boolean {
            return scrollX > 0
        }

        fun smoothClose() {
            smoothScrollTo(0, scrollY)
        }

        fun open() {
            scrollX = swipeMenuWidth
        }

        fun close() {
            scrollX = 0
        }

        override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
            if (ev?.action == MotionEvent.ACTION_UP) {
                toggle()
            }
            return super.dispatchTouchEvent(ev)
        }

        private fun toggle() {
            if (scrollX < swipeMenuWidth / 2) {
                close()
            } else {
                open()
            }
        }

    }
}

class CategoryItem(val time: Long) : Item {
    companion object Controller : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return ViewHolder(parent.inflate(R.layout.recycler_view_item_category, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            item as CategoryItem
            holder as ViewHolder
            val calendar = item.time.toCalendar()
            val weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)
            val weekStr = holder.itemView.resources.getString(R.string.week_of_year_fmt, weekOfYear)
            val yearStr = "" + item.time.toCalendar().get(Calendar.YEAR)
            holder.bind(weekStr, yearStr)
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var weekth: TextView = itemView.weekth
        private var year: TextView = itemView.year

        fun bind(weekstr: String, yearStr: String) {
            weekth.text = weekstr
            year.text = yearStr
        }
    }

    override val controller: ItemController = Controller
}

class CategoryEndItem() : Item {
    companion object Controller : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return ViewHolder(parent.inflate(R.layout.recycler_view_item_category_end, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override val controller: ItemController = Controller
}