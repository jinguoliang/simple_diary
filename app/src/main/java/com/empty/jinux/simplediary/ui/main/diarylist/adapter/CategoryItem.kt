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
import com.empty.jinux.baselibaray.utils.*
import com.empty.jinux.baselibaray.view.recycleview.Item
import com.empty.jinux.baselibaray.view.recycleview.ItemController
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.databinding.RecycleItemDiaryBinding
import com.empty.jinux.simplediary.databinding.RecyclerViewItemCategoryBinding
import java.util.*

class DiaryItem(val data: Diary, val differentDay: Boolean, val onItemListener: OnItemListener) :
    Item {
    interface OnItemListener {
        fun onItemClick(diary: Diary)
        fun onDeleteClick(diary: Diary)
    }

    companion object Controller : ItemController {
        private lateinit var binding: RecycleItemDiaryBinding
        override fun onCreateViewHolder(parent: ViewGroup): androidx.recyclerview.widget.RecyclerView.ViewHolder {
            binding =
                RecycleItemDiaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding.root)
        }

        override fun onBindViewHolder(
            holder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
            item: Item
        ) {
            item as DiaryItem
            holder as ViewHolder
            val diary = item.data
            holder.bind(item.data)
            binding.title.text = diary.diaryContent.getTitleFromContent().takeIf { it.isNotEmpty() }
                ?: holder.itemView.resources.getString(R.string.untitled)
            binding.weekName.text = diary.diaryContent.displayTime.formatToWeekday()
            binding.day.text = diary.diaryContent.displayTime.formatToDay()
            binding.time.text = diary.diaryContent.displayTime.formatToTime()
            holder.showWeekday(item.differentDay)
            holder.originView.setOnClickListener {
                if (holder.isOpen()) {
                    holder.smoothClose()
                } else {
                    item.onItemListener.onItemClick(item.data)
                }
            }
//            binding.sw.setOnClickListener {
//                item.onItemListener.onDeleteClick(
//                    item.data
//                )
//            }
        }

        class ViewHolder(val originView: View) :
            androidx.recyclerview.widget.RecyclerView.ViewHolder(SwipeView(originView)) {
            val title = binding.title
            val weekName: TextView = binding.weekName
            val day: TextView = binding.day
            val time: TextView = binding.time
            val topLine: View = binding.topLine

            fun bind(diary: Diary) {
                closeMenu()

//            currentDiary = diary

            }

            protected fun closeMenu() {
                val tmp = itemView as SwipeView
                tmp.close()
            }

            fun showWeekday(differentDay: Boolean) {
                weekName.visibility = if (differentDay) View.VISIBLE else View.INVISIBLE
                day.visibility = weekName.visibility
//        topLine.visibility = weekName.visibility
                topLine.visibility = View.INVISIBLE
            }

            fun isOpen(): Boolean {
                return (itemView as SwipeView).isOpen()
            }

            fun smoothClose() {

                (itemView as SwipeView).smoothClose()
            }
        }

    }

    override val controller: ItemController = Controller

    @SuppressLint("ViewConstructor")
    class SwipeView(v: View) : HorizontalScrollView(v.context) {
        val swipeMenuWidth = context.dimen(R.dimen.swipe_item_menu_item_width)
        val diariesListHorizontalMargin =
            context.dimen(R.dimen.diaries_list_recycle_view_margin_horizontal)

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
                addView(
                    content, LinearLayout.LayoutParams(
                        context.getScreenWidth() - 2 * diariesListHorizontalMargin,
                        context.dimen(R.dimen.diary_list_item_height)
                    )
                )
                addView(inflateMenuLayout())
            }
            addView(
                linearLayout, FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
            )
        }

        private fun inflateMenuLayout(): View {
            return LayoutInflater.from(context)
                .inflate(R.layout.layout_swipe_item_settings, this, false)
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
        private lateinit var binding: RecyclerViewItemCategoryBinding
        override fun onCreateViewHolder(parent: ViewGroup): androidx.recyclerview.widget.RecyclerView.ViewHolder {
            binding = RecyclerViewItemCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding.root)
        }

        override fun onBindViewHolder(
            holder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
            item: Item
        ) {
            item as CategoryItem
            holder as ViewHolder
            val calendar = item.time.toCalendar()
            val weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)
            val weekStr = holder.itemView.resources.getString(R.string.week_of_year_fmt, weekOfYear)
            val yearStr = "" + item.time.toCalendar().get(Calendar.YEAR)
            binding.weekth.text = weekStr
            binding.year.text = yearStr
        }

    }

    class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView)

    override val controller: ItemController = Controller
}

class CategoryEndItem() : Item {
    companion object Controller : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): androidx.recyclerview.widget.RecyclerView.ViewHolder {
            return ViewHolder(parent.inflate(R.layout.recycler_view_item_category_end, false))
        }

        override fun onBindViewHolder(
            holder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
            item: Item
        ) {
        }

    }

    class ViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
    }

    override val controller: ItemController = Controller
}