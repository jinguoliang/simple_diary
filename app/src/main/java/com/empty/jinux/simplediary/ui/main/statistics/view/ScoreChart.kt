/*
 * Copyright (C) 2016 Álinson Santos Xavier <isoron@gmail.com>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.empty.jinux.simplediary.ui.main.statistics.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.empty.jinux.simplediary.util.AndroidDateFormats
import com.empty.jinux.simplediary.util.DateUtils
import java.text.SimpleDateFormat
import java.util.*

class ScoreChart : ScrollableChart {

    private var pGrid: Paint? = null

    private var em: Float = 0.toFloat()

    private var dfMonth: SimpleDateFormat? = null

    private var dfDay: SimpleDateFormat? = null

    private var dfYear: SimpleDateFormat? = null

    private var pText: Paint? = null
    private var pGraph: Paint? = null

    private var rect: RectF? = null
    private var prevRect: RectF? = null

    private var baseSize: Int = 0

    private var mPaddingTop: Int = 0

    private var columnWidth: Float = 0.toFloat()

    private var columnHeight: Int = 0

    private var nColumns: Int = 0

    private var textColor: Int = 0

    private var gridColor: Int = 0

    private var scores: MutableList<Score>? = null

    private var primaryColor: Int = 0

    @Deprecated("")
    private var bucketSize = 7

    private var mBackgroundColor: Int = 0

    private var mDrawingCache: Bitmap? = null

    private var cacheCanvas: Canvas? = null

    private var isTransparencyEnabled: Boolean = false

    private var skipYear = 0

    private var previousYearText: String? = null

    private var previousMonthText: String? = null

    private val maxDayWidth: Float
        get() {
            var maxDayWidth = 0f
            val day = DateUtils.startOfTodayCalendar

            for (i in 0..27) {
                day.set(Calendar.DAY_OF_MONTH, i)
                val monthWidth = pText!!.measureText(dfMonth!!.format(day.time))
                maxDayWidth = Math.max(maxDayWidth, monthWidth)
            }

            return maxDayWidth
        }

    private val maxMonthWidth: Float
        get() {
            var maxMonthWidth = 0f
            val day = DateUtils.startOfTodayCalendar

            for (i in 0..11) {
                day.set(Calendar.MONTH, i)
                val monthWidth = pText!!.measureText(dfMonth!!.format(day.time))
                maxMonthWidth = Math.max(maxMonthWidth, monthWidth)
            }

            return maxMonthWidth
        }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    fun populateWithRandomData() {
        val random = Random()
        scores = LinkedList()

        var previous = 0.5
        val timestamp = DateUtils.today

        for (i in 1..99) {
            val step = 0.1
            var current = previous + random.nextDouble() * step * 2.0 - step
            current = Math.max(0.0, Math.min(1.0, current))
            scores!!.add(Score(timestamp.minus(i), current))
            previous = current
        }
    }

    @Deprecated("")
    fun setBucketSize(bucketSize: Int) {
        this.bucketSize = bucketSize
        postInvalidate()
    }

    fun setIsTransparencyEnabled(enabled: Boolean) {
        this.isTransparencyEnabled = enabled
        postInvalidate()
    }

    fun setColor(primaryColor: Int) {
        this.primaryColor = primaryColor
        postInvalidate()
    }

    fun setScores(scores: MutableList<Score>) {
        this.scores = scores
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val activeCanvas = if (isTransparencyEnabled) {
            if (mDrawingCache == null) initCache(width, height)

            mDrawingCache!!.eraseColor(Color.TRANSPARENT)
            cacheCanvas
        } else {
            canvas
        }

        if (scores == null) return

        rect!!.set(0f, 0f, nColumns * columnWidth, columnHeight.toFloat())
        rect!!.offset(0f, mPaddingTop.toFloat())

        drawGrid(activeCanvas, rect!!)

        pText!!.color = textColor
        pGraph!!.color = primaryColor
        prevRect!!.setEmpty()

        previousMonthText = ""
        previousYearText = ""
        skipYear = 0

        for (k in 0 until nColumns) {
            val offset = nColumns - k - 1 + dataOffset
            if (offset >= scores!!.size) continue

            val score = scores!![offset].value
            val timestamp = scores!![offset].timestamp

            val height = (columnHeight * score).toInt()

            rect!!.set(0f, 0f, baseSize.toFloat(), baseSize.toFloat())
            rect!!.offset(k * columnWidth + (columnWidth - baseSize) / 2,
                    (mPaddingTop + columnHeight - height - baseSize / 2).toFloat())

            if (!prevRect!!.isEmpty) {
                drawLine(activeCanvas!!, prevRect!!, rect!!)
                drawMarker(activeCanvas, prevRect!!)
            }

            if (k == nColumns - 1) drawMarker(activeCanvas!!, rect!!)

            prevRect!!.set(rect)
            rect!!.set(0f, 0f, columnWidth, columnHeight.toFloat())
            rect!!.offset(k * columnWidth, mPaddingTop.toFloat())

            drawFooter(activeCanvas!!, rect!!, timestamp)
        }

        if (activeCanvas !== canvas) canvas.drawBitmap(mDrawingCache!!, 0f, 0f, null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(width: Int,
                               height: Int,
                               oldWidth: Int,
                               oldHeight: Int) {
        var height = height
        if (height < 9) height = 200

        val maxTextSize = dpToPixels(context, 15f)
        val textSize = height * 0.06f
        pText!!.textSize = Math.min(textSize, maxTextSize)
        em = pText!!.fontSpacing

        val footerHeight = (3 * em).toInt()
        mPaddingTop = em.toInt()

        baseSize = (height - footerHeight - mPaddingTop) / 8
        columnWidth = baseSize.toFloat()
        columnWidth = Math.max(columnWidth, maxDayWidth * 1.5f)
        columnWidth = Math.max(columnWidth, maxMonthWidth * 1.2f)

        nColumns = (width / columnWidth).toInt()
        columnWidth = width.toFloat() / nColumns
        setScrollerBucketSize(columnWidth.toInt())

        columnHeight = 8 * baseSize

        val minStrokeWidth = dpToPixels(context, 1f)
        pGraph!!.textSize = baseSize * 0.5f
        pGraph!!.strokeWidth = baseSize * 0.1f
        pGrid!!.strokeWidth = Math.min(minStrokeWidth, baseSize * 0.05f)

        if (isTransparencyEnabled) initCache(width, height)
    }

    fun dpToPixels(context: Context, dp: Float): Float {

        val resources = context.resources
        val metrics = resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics)
    }

    private fun drawFooter(canvas: Canvas, rect: RectF, currentDate: Timestamp) {
        val yearText = dfYear!!.format(currentDate.toJavaDate())
        val monthText = dfMonth!!.format(currentDate.toJavaDate())
        val dayText = dfDay!!.format(currentDate.toJavaDate())

        val calendar = currentDate.toCalendar()

        val text: String
        val year = calendar.get(Calendar.YEAR)

        var shouldPrintYear = true
        if (yearText == previousYearText) shouldPrintYear = false
        if (bucketSize >= 365 && year % 2 != 0) shouldPrintYear = false

        if (skipYear > 0) {
            skipYear--
            shouldPrintYear = false
        }

        if (shouldPrintYear) {
            previousYearText = yearText
            previousMonthText = ""

            pText!!.textAlign = Paint.Align.CENTER
            canvas.drawText(yearText, rect.centerX(), rect.bottom + em * 2.2f,
                    pText!!)

            skipYear = 1
        }

        if (bucketSize < 365) {
            if (monthText != previousMonthText) {
                previousMonthText = monthText
                text = monthText
            } else {
                text = dayText
            }

            pText!!.textAlign = Paint.Align.CENTER
            canvas.drawText(text, rect.centerX(), rect.bottom + em * 1.2f,
                    pText!!)
        }
    }

    private fun drawGrid(canvas: Canvas?, rGrid: RectF) {
        val nRows = 5
        val rowHeight = rGrid.height() / nRows

        pText!!.textAlign = Paint.Align.LEFT
        pText!!.color = textColor
        pGrid!!.color = gridColor

        for (i in 0 until nRows) {
            canvas!!.drawText(String.format("%d%%", 100 - i * 100 / nRows),
                    rGrid.left + 0.5f * em, rGrid.top + 1f * em, pText!!)
            canvas.drawLine(rGrid.left, rGrid.top, rGrid.right, rGrid.top,
                    pGrid!!)
            rGrid.offset(0f, rowHeight)
        }

        canvas!!.drawLine(rGrid.left, rGrid.top, rGrid.right, rGrid.top, pGrid!!)
    }

    private fun drawLine(canvas: Canvas, rectFrom: RectF, rectTo: RectF) {
        pGraph!!.color = primaryColor
        canvas.drawLine(rectFrom.centerX(), rectFrom.centerY(),
                rectTo.centerX(), rectTo.centerY(), pGraph!!)
    }

    private fun drawMarker(canvas: Canvas, rect: RectF) {
        rect.inset(baseSize * 0.225f, baseSize * 0.225f)
        setModeOrColor(pGraph, XFERMODE_CLEAR, mBackgroundColor)
        canvas.drawOval(rect, pGraph!!)

        rect.inset(baseSize * 0.1f, baseSize * 0.1f)
        setModeOrColor(pGraph, XFERMODE_SRC, primaryColor)
        canvas.drawOval(rect, pGraph!!)

        //        rect.inset(baseSize * 0.1f, baseSize * 0.1f);
        //        setModeOrColor(pGraph, XFERMODE_CLEAR, mBackgroundColor);
        //        canvas.drawOval(rect, pGraph);

        if (isTransparencyEnabled) pGraph!!.xfermode = XFERMODE_SRC
    }

    private fun init() {
        initPaints()
        initColors()
        initDateFormats()
        initRects()
    }

    private fun initCache(width: Int, height: Int) {
        if (mDrawingCache != null) mDrawingCache!!.recycle()
        mDrawingCache = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        cacheCanvas = Canvas(mDrawingCache!!)
    }

    private fun initColors() {

        primaryColor = Color.BLACK
        textColor = Color.RED
        gridColor = Color.CYAN
        mBackgroundColor = Color.WHITE
    }

    private fun initDateFormats() {
        dfYear = AndroidDateFormats.fromSkeleton("yyyy")
        dfMonth = AndroidDateFormats.fromSkeleton("MMM")
        dfDay = AndroidDateFormats.fromSkeleton("d")
    }

    private fun initPaints() {
        pText = Paint()
        pText!!.isAntiAlias = true

        pGraph = Paint()
        pGraph!!.textAlign = Paint.Align.CENTER
        pGraph!!.isAntiAlias = true

        pGrid = Paint()
        pGrid!!.isAntiAlias = true
    }

    private fun initRects() {
        rect = RectF()
        prevRect = RectF()
    }

    private fun setModeOrColor(p: Paint?, mode: PorterDuffXfermode, color: Int) {
        if (isTransparencyEnabled)
            p!!.xfermode = mode
        else
            p!!.color = color
    }

    companion object {
        private val XFERMODE_CLEAR = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

        private val XFERMODE_SRC = PorterDuffXfermode(PorterDuff.Mode.SRC)
    }
}
