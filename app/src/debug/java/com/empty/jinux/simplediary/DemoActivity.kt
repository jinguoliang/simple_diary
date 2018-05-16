package com.empty.jinux.simplediary

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.*
import android.text.style.*
import com.empty.jinux.simplediary.util.ThreadPools
import com.empty.jinux.simplediary.util.adjustParagraphSpace
import kotlinx.android.synthetic.main.activity_demo.*
import org.jetbrains.anko.append

class DemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)
        editor.text = SpannableStringBuilder().apply {
            val drawable = ContextCompat.getDrawable(this@DemoActivity, R.drawable.ic_drawer_top_bg)!!
            drawable.bounds = Rect(0, 0, 500, 500)
            append("a123123", ImageSpan(drawable))
            append("helloo", BackgroundColorSpan(Color.CYAN))
            append("dangdang", object : MetricAffectingSpan() {
                override fun updateMeasureState(p: TextPaint) {
                    p.baselineShift -= 130
                }

                override fun updateDrawState(p: TextPaint) {
                    p.baselineShift -= 130
                }

            })

        }

        editor.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                editor.adjustParagraphSpace(s)
            }

        })
        ThreadPools.postOnUIDelayed(15000) {


        }
    }


}