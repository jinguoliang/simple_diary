package com.empty.jinux.simplediary.ui.settings

import android.content.Context
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceViewHolder
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.empty.jinux.baselibaray.utils.inflate
import com.empty.jinux.baselibaray.view.recycleview.Item
import com.empty.jinux.baselibaray.view.recycleview.ItemController
import com.empty.jinux.baselibaray.view.recycleview.withItems
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.ui.diarydetail.fragment.edittools.EditorStyle
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_font_selector.*

class FontSelectPreference : Preference {
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        layoutResource = R.layout.style_select_preference
        widgetLayoutResource = R.layout.layout_style_select_widget
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val selector = holder.findViewById(R.id.styleRecyclerView) as RecyclerView
        loadData(selector)
    }

    private fun loadData(selector: RecyclerView) {
        selector.withItems {
            val selected = getPersistedString(EditorFontSize.DEFAULT)
            EditorFontSize.NAMES.map { EditorFontSize(it) }
                    .map {
                        FontSelectItem(it, selected == it.name) {
                            persistString(it)
                            loadData(selector)
                        }
                    }
                    .forEach { add(it) }
        }
    }

}

class FontSelectItem(val style: EditorFontSize, val selected: Boolean, val onSelect: (name: String) -> Unit) : Item {
    companion object Controller : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return Holder(parent.inflate(R.layout.item_font_selector, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as Holder
            item as FontSelectItem
            holder.fontSizeView.textSize = item.style.size
            holder.frameView.visibility = if (item.selected) View.VISIBLE else View.INVISIBLE
            holder.itemView.setOnClickListener { item.onSelect(item.style.name) }
        }

        class Holder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer
    }

    override val controller = Controller

}