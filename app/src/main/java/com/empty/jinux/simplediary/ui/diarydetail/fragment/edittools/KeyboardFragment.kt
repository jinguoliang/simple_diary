package com.empty.jinux.simplediary.ui.diarydetail.fragment.edittools

import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.ui.diarydetail.fragment.MFragment

class KeyboardFragment : MFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return View(context).apply {
            setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }
}