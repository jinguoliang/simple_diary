package com.empty.jinux.simplediary.ui.main

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.updateLayoutParams
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.util.getStatusBarHeight

class MImageView : AppCompatImageView {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }



    private fun init() {
        // todo: why post
        post {
            updateLayoutParams<ViewGroup.MarginLayoutParams> {
                top = (context as? Activity)?.getStatusBarHeight() ?: context.resources.getDimensionPixelOffset(R.dimen.statusbar_height)
            }
        }
    }
}