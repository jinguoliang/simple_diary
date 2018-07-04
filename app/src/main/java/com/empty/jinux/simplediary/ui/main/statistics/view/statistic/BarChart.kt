package com.empty.jinux.simplediary.ui.main.statistics.view.statistic

import android.content.Context
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.empty.jinux.baselibaray.utils.inflate
import com.empty.jinux.baselibaray.utils.layoutHeight
import com.empty.jinux.baselibaray.utils.layoutWidth
import com.empty.jinux.baselibaray.view.recycleview.Item
import com.empty.jinux.baselibaray.view.recycleview.ItemController
import com.empty.jinux.baselibaray.view.recycleview.withItems
import com.empty.jinux.simplediary.R
import kotlinx.android.synthetic.main.recycler_view_bar_item.view.*
import org.jetbrains.anko.dimen

class BarChart : FrameLayout {
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Bar.barWidth = (measuredWidth - paddingStart - paddingEnd) / 7
        Bar.maxBarHeight = (measuredHeight - paddingTop - 2 * dimen(R.dimen.bar_item_value_textview_height))
    }

    val recyclerView = RecyclerView(context).also {
        it.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    val emptyView = ImageView(context).also {
        it.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_01d, null))
    }

    init {
        recyclerView.visibility = View.INVISIBLE
        addView(recyclerView, FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).also { it.gravity = Gravity.CENTER })
        addView(emptyView, FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).also { it.gravity = Gravity.CENTER })
    }

    interface Formater {
        fun format(v: Long): String
    }

    fun setXAxisValueFormater(formater: Formater) {
        Bar.xAxisFormatter = formater
    }

    fun setData(data: List<Pair<Long, Long>>): Unit {

        if (data.isEmpty()) {
            recyclerView.visibility = View.INVISIBLE
            emptyView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.INVISIBLE

            data.maxBy { it.second }?.apply { Bar.maxYValue = second }
            recyclerView.withItems {
                addAll(data.map { Bar(it) })
            }
        }

    }

    data class Data(val map: HashMap<Long, Long>)
}

class Bar(val data: Pair<Long, Long>) : Item {
    companion object Controller : ItemController {
        var barWidth = 0
        var maxBarHeight = 0
        var maxYValue = 0L

        var xAxisFormatter: BarChart.Formater? = null

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return Holder(parent.inflate(R.layout.recycler_view_bar_item, false).also {
                it.layoutWidth = barWidth
            })
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as Holder
            item as Bar
            holder.xValueTv.text = xAxisFormatter?.format(item.data.first) ?: "no formatter"
            holder.yValueTv.text = item.data.second.toString()
            holder.bar.layoutHeight = ((item.data.second.toFloat() / maxYValue) * maxBarHeight).toInt()
        }

        class Holder(v: View) : RecyclerView.ViewHolder(v) {
            val xValueTv = v.xValueTv
            val yValueTv = v.yValueTv
            val bar = v.bar
        }

    }

    override val controller = Controller
}