package com.empty.jinux.baselibaray.view.recycleview

import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView

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
            holder.itemView.updateLayoutParams {
                height = item.size
            }
        }
    }

    private class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)
}