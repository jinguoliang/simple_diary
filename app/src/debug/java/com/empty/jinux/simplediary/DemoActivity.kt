package com.empty.jinux.simplediary

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.text.*
import android.text.style.*
import com.empty.jinux.baselibaray.thread.ThreadPools
import com.empty.jinux.baselibaray.utils.adjustParagraphSpace
import com.empty.jinux.simplediary.databinding.ActivityDemoBinding

class DemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDemoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_demo)
        binding.mEditor.text = SpannableStringBuilder().apply {
            val drawable = ContextCompat.getDrawable(this@DemoActivity, R.drawable.ic_drawer_top_bg)!!
            drawable.bounds = Rect(0, 0, 500, 500)
//            append("a123123", ImageSpan(drawable))
//            append("helloo", BackgroundColorSpan(Color.CYAN))
//            append("dangdang", object : MetricAffectingSpan() {
//                override fun updateMeasureState(p: TextPaint) {
//                    p.baselineShift -= 130
//                }
//
//                override fun updateDrawState(p: TextPaint) {
//                    p.baselineShift -= 130
//                }
//
//            })

        }

        binding.mEditor.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.mEditor.adjustParagraphSpace(R.dimen.editor_paragraph_end)
            }

        })
        ThreadPools.postOnUIDelayed(15000) {


        }
    }


}