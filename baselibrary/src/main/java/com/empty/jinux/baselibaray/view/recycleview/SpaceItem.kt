package com.empty.jinux.baselibaray.view.recycleview

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.empty.jinux.baselibaray.utils.layoutHeight

class SpaceItem(val size: Int) : Item {
    override val controller: ItemController
        get() = Controller

    private companion object Controller : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): androidx.recyclerview.widget.RecyclerView.ViewHolder {
            return ViewHolder(View(parent.context).also {
                it.layoutParams = androidx.recyclerview.widget.RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
            })
        }

        override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, item: Item) {
            item as SpaceItem
            holder.itemView.layoutHeight = item.size
        }
    }

    private class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)
}