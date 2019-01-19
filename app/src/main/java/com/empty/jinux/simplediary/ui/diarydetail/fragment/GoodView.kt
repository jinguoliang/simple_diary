package com.empty.jinux.simplediary.ui.diarydetail.fragment

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.animation.addListener
import com.empty.jinux.baselibaray.log.loge
import com.empty.jinux.baselibaray.utils.CountDownTimer
import com.empty.jinux.simplediary.R
import kotlinx.android.synthetic.main.activity_diary_detail.*
import org.jetbrains.anko.dimen

class GoodView : FrameLayout {
    companion object {

        const val DURATION = 500L
        var maxSize: Float? = null
        var hafWidth: Float? = null
        var hafHeight: Float? = null
        var paint = Paint()
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    var currentRadius: Float? = null
    var currentCenterX: Float? = null
    var currentCenterY: Float? = null
    val checkDrawableSize = dimen(R.dimen.good_view_check_size)
    val checkDrawable = VectorDrawableCompat.create(resources, R.drawable.ic_thumb_up_black_24dp, null)

    override fun dispatchDraw(canvas: Canvas) {

        if (currentRadius == null) {
            if (maxSize == null) {
                hafWidth = width.toFloat() / 2
                hafHeight = height.toFloat() / 2
                maxSize = Math.sqrt(Math.pow(hafWidth!!.toDouble(), 2.toDouble()) + Math.pow(hafHeight!!.toDouble(), 2.toDouble())).toFloat()
                paint.color = resources.getColor(R.color.colorPrimary)
            }

            currentCenterX = hafWidth
            currentCenterY = hafHeight

            startCheckAnim()
        }

        canvas.save()
        canvas.translate(currentCenterX!!, currentCenterY!!)
        canvas.drawCircle(0f, 0f, currentRadius!!, paint)
        checkDrawable?.setBounds(-checkDrawableSize, -checkDrawableSize, checkDrawableSize, checkDrawableSize)
        checkDrawable?.draw(canvas)
        canvas.restore()

        super.dispatchDraw(canvas)
    }

    override fun onVisibilityChanged(changedView: View?, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == View.GONE) {
            currentRadius = null
        }
    }

    private fun startCheckAnim() {
        val minSize = context.resources.getDimension(R.dimen.good_view_circle_size)
        val anim = ValueAnimator.ofFloat(maxSize!!, minSize)
        anim.duration = DURATION
        anim.addUpdateListener {
            currentRadius = it.animatedValue as Float
            invalidate()
        }
        anim.addListener(onEnd = {
            loge("anim end")
            setOnClickListener {
                check()
            }
            CountDownTimer.countDownToDo(2000) {
                check()
            }
        })
        anim.start()
    }

    private fun check() {
        val actionbarCheck = (context as Activity).action_check
        actionbarCheck.alpha = 0f
        actionbarCheck.visibility = View.VISIBLE
        val rect = Rect()
        actionbarCheck.getGlobalVisibleRect(rect)

        ValueAnimator.ofFloat(currentCenterX!!, rect.centerX().toFloat()).apply {
            addUpdateListener {
                currentCenterX = it.animatedValue as Float
            }
            start()
        }

        val rect1 = Rect()
        getGlobalVisibleRect(rect1)
        ValueAnimator.ofFloat(currentCenterY!!, rect.centerY().toFloat() - rect1.top).apply {
            addUpdateListener {
                currentCenterY = it.animatedValue as Float
                invalidate()
            }
            addListener(onEnd = {
                actionbarCheck.animate().alpha(1f).start()
                visibility = View.GONE
            })
            start()
        }
    }

}