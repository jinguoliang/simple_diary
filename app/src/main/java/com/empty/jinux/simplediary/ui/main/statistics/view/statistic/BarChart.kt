package com.empty.jinux.simplediary.ui.main.statistics.view.statistic

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.empty.jinux.baselibaray.thread.ThreadPools
import com.empty.jinux.baselibaray.utils.dimen
import com.empty.jinux.baselibaray.utils.inflate
import com.empty.jinux.baselibaray.utils.layoutHeight
import com.empty.jinux.baselibaray.utils.layoutWidth
import com.empty.jinux.baselibaray.view.recycleview.Item
import com.empty.jinux.baselibaray.view.recycleview.ItemController
import com.empty.jinux.baselibaray.view.recycleview.replaceData
import com.empty.jinux.baselibaray.view.recycleview.withItems
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.databinding.RecyclerViewBarItemBinding

class BarChart : FrameLayout {
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    val recyclerView = RecyclerView(context).also {
        it.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            context,
            androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
            false
        )
    }

    private val emptyView = ImageView(context).also {
    }

    private val items = mutableListOf<Item>()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Bar.barWidth = (measuredWidth - paddingStart - paddingEnd) / 7
        Bar.maxBarHeight =
            (measuredHeight - paddingTop - paddingBottom - 2 * context.dimen(R.dimen.bar_item_value_textview_height))
    }


    init {
        recyclerView.withItems(items)
        recyclerView.visibility = View.INVISIBLE
        addView(
            recyclerView,
            FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                .also { it.gravity = Gravity.CENTER })
        addView(
            emptyView,
            FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                .also { it.gravity = Gravity.CENTER })
    }

    interface Formator {
        fun format(v: Long): String
    }

    fun setXAxisValueFormator(formator: Formator) {
        Bar.xAxisFormatter = formator
    }

    fun setData(data: List<Pair<Long, Long>>) {

        if (data.isEmpty()) {
            recyclerView.visibility = View.INVISIBLE
            emptyView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.INVISIBLE

            data.maxBy { it.second }?.apply { Bar.maxYValue = second }

            recyclerView.replaceData(data.map { Bar(it) })

            ThreadPools.postOnUIDelayed(500) {
                recyclerView.smoothScrollToPosition(data.size)
            }
        }

    }
}

class Bar(val data: Pair<Long, Long>) : Item {
    companion object Controller : ItemController {
        var barWidth = 0
        var maxBarHeight = 0
        var maxYValue = 0L

        var xAxisFormatter: BarChart.Formator? = null

        private lateinit var binding: RecyclerViewBarItemBinding

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            binding = RecyclerViewBarItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return Holder(binding.root.also {
                it.layoutWidth = barWidth
            })
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as Holder
            item as Bar
            binding.xValueTv.text = xAxisFormatter?.format(item.data.first) ?: "no formatter"
            binding.yValueTv.text = item.data.second.toString()
            binding.bar.layoutHeight = ((item.data.second.toFloat() / maxYValue) * maxBarHeight).toInt()
        }

        class Holder(containerView: View) : RecyclerView.ViewHolder(containerView)
    }

    override val controller = Controller
}