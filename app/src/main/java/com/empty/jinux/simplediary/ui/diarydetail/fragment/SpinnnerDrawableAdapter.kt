package com.empty.jinux.simplediary.ui.diarydetail.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.empty.jinux.simplediary.R

/**
 * Created by jinux on 18-4-13.
 */
open class SpinnnerDrawableAdapter(context: Context?,
                                   val spinnerRes: Int,
                                   val dropdownItemRes: Int,
                                   itemArray: List<Int>)
    : ArrayAdapter<Int>(context, 0, itemArray) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createView(convertView, spinnerRes, parent, position)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createView(convertView, dropdownItemRes, parent, position)
    }

    private fun createView(convertView: View?, itemRes: Int, parent: ViewGroup?, position: Int): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(itemRes, parent, false)
        val image = view.findViewById<ImageView>(R.id.list_item)
        val item = getItem(position)
        image.setImageResource(item)
        return view
    }
}