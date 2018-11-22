package com.empty.jinux.simplediary.ui.diarydetail.fragment

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.graphics.Rect
import android.util.Log
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.FrameLayout
import com.empty.jinux.baselibaray.log.logw

class KeyboardHeightProvider(private val activity: Activity) {

    private var keyboardLandscapeHeight: Int = 0

    private var keyboardPortraitHeight: Int = 0

    private val screenOrientation: Int
        get() = activity.resources.configuration.orientation


    private val contentView = FrameLayout(activity)

    private val wm = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val params = WindowManager.LayoutParams().apply {
        width = 0
        height = WindowManager.LayoutParams.MATCH_PARENT
        softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        type = WindowManager.LayoutParams.TYPE_APPLICATION
        try {
            token = activity.reflectFeild(Activity::class.java, "mToken")
        } catch (e: Exception) {
            Log.e(Log.getStackTraceString(e), TAG)
        }
    }

    private var isShowing: Boolean = false


    val onLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            handleOnGlobalLayout()
        }
    }

    /**
     * Start the KeyboardHeightProvider, this must be called after the onResume of the Activity.
     * PopupWindows are not allowed to be registered before the onResume has finished
     * of the Activity.
     */
    fun start() {
        contentView.viewTreeObserver.addOnGlobalLayoutListener(onLayoutListener)
        if (!isShowing) {
            isShowing = true
            try {
                wm.addView(contentView, params)
            } catch (e: Exception) {
                logw(Log.getStackTraceString(e), TAG)
                contentView.viewTreeObserver.removeOnGlobalLayoutListener(onLayoutListener)
            }
        }
    }

    fun close() {
        try {
            wm.removeView(contentView)
            contentView.viewTreeObserver.removeOnGlobalLayoutListener(onLayoutListener)
        } catch (e: Exception) {
            logw(Log.getStackTraceString(e), TAG)
        } finally {
            isShowing = false
        }
    }

    /**
     * Popup window itself is as big as the window of the Activity.
     * The keyboard can then be calculated by extracting the popup view bottom
     * from the activity_main window height.
     */
    private fun handleOnGlobalLayout() {

        val screenSize = Point()
        activity.windowManager.defaultDisplay.getSize(screenSize)

        val rect = Rect()
        contentView.getWindowVisibleDisplayFrame(rect)

        // REMIND, you may like to change this using the fullscreen size of the phone
        // and also using the status bar and navigation bar heights of the phone to calculate
        // the keyboard height. But this worked fine on a Nexus.
        val orientation = screenOrientation
        val keyboardHeight = screenSize.y - rect.bottom

        if (keyboardHeight == 0) {
            notifyKeyboardHeightChanged(0, orientation)
        } else {
            val statusbarHeight = 0
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                this.keyboardPortraitHeight = keyboardHeight - statusbarHeight
                notifyKeyboardHeightChanged(keyboardPortraitHeight, orientation)
            } else {
                this.keyboardLandscapeHeight = keyboardHeight - statusbarHeight
                notifyKeyboardHeightChanged(keyboardLandscapeHeight, orientation)
            }
        }
    }

    var observer: KeyboardHeightObserver? = null

    private fun notifyKeyboardHeightChanged(height: Int, orientation: Int) {
        observer?.onKeyboardHeightChanged(height, orientation)
    }

    companion object {
        const val TAG = "KeyboardHeightProvider"
    }
}

interface KeyboardHeightObserver {

    /**
     * Called when the keyboard height has changed, 0 means keyboard is closed,
     * >= 1 means keyboard is opened.
     *
     * @param height        The height of the keyboard in pixels
     * @param orientation   The orientation either: Configuration.ORIENTATION_PORTRAIT or
     * Configuration.ORIENTATION_LANDSCAPE
     */
    fun onKeyboardHeightChanged(height: Int, orientation: Int)
}