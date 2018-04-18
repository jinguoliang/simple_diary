package com.empty.jinux.simplediary.ui.main.statistics.view.punchcard

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import com.empty.jinux.simplediary.R
import kotlinx.android.synthetic.main.layout_punchcard.view.*
import me.drakeet.multitype.MultiTypeAdapter

class PunchCard : CardView {

    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.layout_punchcard, this)
        punchRecycleView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        punchRecycleView.adapter = initAdapter()
    }

    private fun initAdapter(): RecyclerView.Adapter<*>? {
        return MultiTypeAdapter().apply {
            register(PunchCheckItem::class.java, PunchCheckBinder())
        }
    }

    fun setWordCountOfEveryday(counts: List<PunchCheckItem>) {
        val adapter = punchRecycleView.adapter as MultiTypeAdapter
        adapter.items = counts
        adapter.notifyDataSetChanged()
    }

}