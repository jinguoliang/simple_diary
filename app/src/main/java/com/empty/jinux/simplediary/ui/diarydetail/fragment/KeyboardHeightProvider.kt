package com.empty.jinux.simplediary.ui.diarydetail.fragment

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.PopupWindow

class KeyboardHeightProvider
/**
 * Construct a new KeyboardHeightProvider
 *
 * @param activity The parent activity_main
 */
(private val activity: Activity) : PopupWindow(activity) {

    /** The cached landscape height of the keyboard  */
    private var keyboardLandscapeHeight: Int = 0

    /** The cached portrait height of the keyboard  */
    private var keyboardPortraitHeight: Int = 0

    /** The parent view  */
    private val parentView: View

    /**
     * Get the screen orientation
     *
     * @return the screen orientation
     */
    private val screenOrientation: Int
        get() = activity.resources.configuration.orientation

    init {
        contentView = FrameLayout(activity)

        softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        inputMethodMode = INPUT_METHOD_NEEDED

        parentView = activity.findViewById(android.R.id.content)

        width = 0
        height = WindowManager.LayoutParams.MATCH_PARENT

        contentView.viewTreeObserver.addOnGlobalLayoutListener {
            if (contentView != null) {
                handleOnGlobalLayout()
            }
        }
    }

    /**
     * Start the KeyboardHeightProvider, this must be called after the onResume of the Activity.
     * PopupWindows are not allowed to be registered before the onResume has finished
     * of the Activity.
     */
    fun start() {

        if (!isShowing && parentView.windowToken != null) {
            setBackgroundDrawable(ColorDrawable(Color.RED))
            showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0)
        }
    }

    /**
     * Close the keyboard height provider,
     * this provider will not be used anymore.
     */
    fun close() {
        dismiss()
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
//            val statusbarHeight = activity.window.getStatusBarHeight()
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

    /**
     *
     */
    private fun notifyKeyboardHeightChanged(height: Int, orientation: Int) {
        if (observer != null) {
            observer!!.onKeyboardHeightChanged(height, orientation)
        }
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