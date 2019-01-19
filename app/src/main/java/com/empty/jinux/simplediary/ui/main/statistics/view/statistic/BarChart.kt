package com.empty.jinux.simplediary.ui.main.statistics.view.statistic

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.empty.jinux.baselibaray.thread.ThreadPools
import com.empty.jinux.baselibaray.utils.inflate
import com.empty.jinux.baselibaray.view.recycleview.Item
import com.empty.jinux.baselibaray.view.recycleview.ItemController
import com.empty.jinux.baselibaray.view.recycleview.replaceData
import com.empty.jinux.baselibaray.view.recycleview.withItems
import com.empty.jinux.simplediary.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.recycler_view_bar_item.*
import org.jetbrains.anko.dimen

class BarChart : FrameLayout {
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val recyclerView = RecyclerView(context).also {
        it.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private val emptyView = ImageView(context).also {
    }

    private val items = mutableListOf<Item>()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Bar.barWidth = (measuredWidth - paddingStart - paddingEnd) / 7
        Bar.maxBarHeight = (measuredHeight - paddingTop - paddingBottom - 2 * dimen(R.dimen.bar_item_value_textview_height))
    }


    init {
        recyclerView.withItems(items)
        recyclerView.visibility = View.INVISIBLE
        addView(recyclerView, FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).also { it.gravity = Gravity.CENTER })
        addView(emptyView, FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).also { it.gravity = Gravity.CENTER })
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

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return Holder(parent.inflate(R.layout.recycler_view_bar_item, false).also {
                it.updateLayoutParams {
                    width = barWidth
                }
            })
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as Holder
            item as Bar
            holder.xValueTv.text = xAxisFormatter?.format(item.data.first) ?: "no formatter"
            holder.yValueTv.text = item.data.second.toString()

            holder.bar.updateLayoutParams {
                height = ((item.data.second.toFloat() / maxYValue) * maxBarHeight).toInt()
            }
        }

        class Holder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer
    }

    override val controller = Controller
}