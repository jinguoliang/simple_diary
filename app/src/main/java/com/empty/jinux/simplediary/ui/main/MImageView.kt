package com.empty.jinux.simplediary.ui.main

import android.app.Activity
import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.util.getStatusBarHeight
import com.empty.jinux.baselibaray.utils.layoutTop

class MImageView : AppCompatImageView {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }


    private fun init() {
        post {
            layoutTop = (context as? Activity)?.getStatusBarHeight() ?: context.resources.getDimensionPixelOffset(R.dimen.statusbar_height)
        }
    }
}