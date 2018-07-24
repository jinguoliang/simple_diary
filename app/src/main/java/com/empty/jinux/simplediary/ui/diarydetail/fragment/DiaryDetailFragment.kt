/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.empty.jinux.simplediary.ui.diarydetail.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.res.ResourcesCompat
import android.text.Editable
import android.text.Selection
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.empty.jinux.baselibaray.log.loge
import com.empty.jinux.baselibaray.log.logi
import com.empty.jinux.baselibaray.thread.ThreadPools
import com.empty.jinux.baselibaray.utils.TextWatcherAdapter
import com.empty.jinux.baselibaray.utils.adjustParagraphSpace
import com.empty.jinux.baselibaray.utils.dpToPx
import com.empty.jinux.baselibaray.utils.getScaleImage
import com.empty.jinux.baselibaray.utils.hideInputMethod
import com.empty.jinux.baselibaray.utils.layoutHeight
import com.empty.jinux.baselibaray.utils.showInputMethod
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.config.ConfigManager
import com.empty.jinux.simplediary.data.INVALID_DIARY_ID
import com.empty.jinux.simplediary.data.LocationInfo
import com.empty.jinux.simplediary.intent.shareContentIntent
import com.empty.jinux.simplediary.report.Reporter
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailActivity
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailContract
import com.empty.jinux.simplediary.ui.diarydetail.fragment.edittools.EditorStyle
import com.empty.jinux.simplediary.ui.diarydetail.fragment.edittools.KeyboardFragment
import com.empty.jinux.simplediary.ui.diarydetail.fragment.edittools.PictureSelectFragment
import com.empty.jinux.simplediary.ui.diarydetail.fragment.edittools.StatusFragment
import com.empty.jinux.simplediary.ui.diarydetail.presenter.DiaryDetailPresenter
import com.empty.jinux.simplediary.ui.settings.EditorFontSize
import com.empty.jinux.simplediary.util.PermissionUtil
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.activity_diary_detail.*
import kotlinx.android.synthetic.main.fragment_taskdetail.*
import kotlinx.android.synthetic.main.layout_diary_detail_edit_tool.*
import org.jetbrains.anko.contentView
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.dimen
import org.jetbrains.anko.dip
import org.jetbrains.anko.toast
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


/**
 * Main UI for the task detail screen.
 */
class DiaryDetailFragment : DaggerFragment(), DiaryDetailContract.View {

    @Inject
    internal
    lateinit var mPresenter: DiaryDetailPresenter

    @Inject
    internal
    lateinit var mReporter: Reporter

    @Inject
    internal
    lateinit var mConfig: ConfigManager

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        val wordCountToday = arguments?.getInt(DiaryDetailActivity.EXTRA_TODAY_WORD_COUNT, 0)
                ?: 0
        val taskId = arguments?.getLong(DiaryDetailActivity.EXTRA_DIARY_ID, INVALID_DIARY_ID)
                ?: INVALID_DIARY_ID
        mPresenter.setDiaryId(taskId)
        mPresenter.setWordCountToday(wordCountToday)
    }

    override val isActive: Boolean
        get() = isAdded

    override fun onResume() {
        logi("detail fragment onResume", "detail")
        super.onResume()

        diaryContent.addTextChangedListener(mWatcher)

        ThreadPools.postOnUI {
            keyboardHeightListener.start()
        }
    }

    override fun onPause() {
        logi("detail fragment onPause", "detail")
        super.onPause()
        keyboardHeightListener.close()
        diaryContent.removeTextChangedListener(mWatcher)
        mPresenter.stop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mPresenter.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mKeyboardHeightCached > 0) {
            mConfig.put(CONFIG_KEY_KEYBOARD_HEIGHT, mKeyboardHeightCached)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_taskdetail, container, false)
    }

    private var mWatcher: TextWatcher? = null

    lateinit var keyboardHeightListener: KeyboardHeightProvider

    var editFontSize = 25f
    lateinit var editStyle: EditorStyle


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)

        editFontSize = EditorFontSize(context!!.defaultSharedPreferences.getString(getString(R.string.pref_default_font_size), EditorFontSize.DEFAULT)).size
        editStyle = EditorStyle(context!!, context!!.defaultSharedPreferences.getString(getString(R.string.pref_default_editor_style), "heiyaoshi"))


        setupEditView()
        setupContainer()
        initEditToolbar()
        setupKeyboardHeightListener()

        savedInstanceState?.getLong(KEY_DIARY_ID)?.apply {
            mPresenter.setDiaryId(this)
        }

        mPresenter.start()
    }

    private fun setupKeyboardHeightListener() {
        keyboardHeightListener = KeyboardHeightProvider(activity!!)
        keyboardHeightListener.observer = object : KeyboardHeightObserver {
            override fun onKeyboardHeightChanged(height: Int, orientation: Int) {
                if (diaryContent == null) {
                    return
                }

                val inputMethodShowed = height != 0
                if (inputMethodShowed) {
                    onInputMethodShowed(height)
                } else {
                    onInputMedhodHided()
                }
            }
        }
    }

    private fun setupContainer() {
        editContainer.setOnClickListener {
            diaryContent.apply { setSelection(text.length) }
            showInputMethod()
        }
        fragmentContainer.setOnClickListener {
            diaryContent.apply { setSelection(text.length) }
            showInputMethod()
        }
    }

    private fun setupEditView() {
        mWatcher = object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable?) {
                mPresenter.onContentChange(s.toString())
                formatEditContent()
            }
        }
        diaryContent.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_UP -> {
                    diaryContent.isLongClickable = true
                }
                MotionEvent.ACTION_MOVE -> diaryContent.isLongClickable = false
            }
            false
        }

        diaryContent.movementMethod = object : LinkMovementMethod() {
            override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
                val action = event.action

                if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
                    var x = event.getX().toInt()
                    var y = event.getY().toInt()

                    x -= widget.getTotalPaddingLeft()
                    y -= widget.getTotalPaddingTop()

                    x += widget.getScrollX()
                    y += widget.getScrollY()

                    val layout = widget.getLayout()
                    val line = layout.getLineForVertical(y)
                    val off = layout.getOffsetForHorizontal(line, x.toFloat())

                    val links = buffer.getSpans(off, off, ClickableSpan::class.java)

                    if (links.isNotEmpty()) {
                        if (action == MotionEvent.ACTION_UP) {
                            links[0].onClick(widget)
                        } else if (action == MotionEvent.ACTION_DOWN) {
//                                Selection.setSelection(buffer,
//                                        buffer.getSpanStart(links[0]),
//                                        buffer.getSpanEnd(links[0]))
                        }
                        return true
                    } else {
                        Selection.removeSelection(buffer)
                    }
                }

                return super.onTouchEvent(widget, buffer, event)
            }
        }

        diaryContent.mScrollParent = scrollContainer
        diaryContent.textSize = editFontSize
        activity?.contentView?.background = editStyle.background
        diaryContent.setTextColor(editStyle.fontColor)
        diaryContent.cursorColor = editStyle.cursorColor
    }

    private fun onInputMedhodHided() {
        if (editToolsTab.selectedTabPosition == 0) {
            hideToolArea()
        }
    }

    override fun showGoodView(): Boolean {
        goodView.visibility = View.VISIBLE
        return true
    }

    override fun setTodayGood(show: Boolean) {
        val actionbarCheck = (context as Activity).action_check
        actionbarCheck.visibility = if (show) View.VISIBLE else View.INVISIBLE
        if (!show) {
            goodView.visibility = View.GONE
        }
    }

    var mKeyboardHeightCached = 0

    private fun onInputMethodShowed(height: Int) {
        mKeyboardHeightCached = height

        diaryContent.isCursorVisible = true

        setGoodViewHeight(height)
        setToolAreaHeight(height)
        toolArea.setCurrentItem(0, false)
        showToolArea()

        ThreadPools.postOnUI {
            diaryContent.adjustScrollPosition(scrollContainer, editTabContainer.top - context!!.dpToPx(editFontSize / 2) - diaryContent.paddingTop)
        }
    }

    private fun setGoodViewHeight(keyboardHeight: Int) {
        goodView.layoutHeight = fragmentContainer.height - keyboardHeight
    }

    private fun setToolAreaHeight(height: Int) {
        if (height != toolArea.layoutHeight) {
            bottomSpace.layoutHeight = height + toolArea.dimen(R.dimen.diary_detail_edit_tool_height)
            toolArea.layoutHeight = height
        }
    }

    private lateinit var keyboardFragment: KeyboardFragment

    private lateinit var statusFragment: StatusFragment

    private lateinit var selectPictureFragment: PictureSelectFragment

    private fun initEditToolbar() {
        keyboardFragment = ((fragmentManager?.findFragmentByTag(makeFragmentName(toolArea.id, 0)) as? KeyboardFragment)
                ?: KeyboardFragment())

        statusFragment = ((fragmentManager?.findFragmentByTag(makeFragmentName(toolArea.id, 1)) as? StatusFragment)
                ?: StatusFragment())

        selectPictureFragment = ((fragmentManager?.findFragmentByTag(makeFragmentName(toolArea.id, 2)) as? PictureSelectFragment)
                ?: PictureSelectFragment())

        val fragments = listOf(keyboardFragment, statusFragment, selectPictureFragment)
        fragments.forEach {
            it.mPresenter = mPresenter
            it.mReporter = mReporter
            it.mParentFragment = this@DiaryDetailFragment
        }

        toolArea.adapter = object : FragmentPagerAdapter(fragmentManager) {
            override fun getItem(position: Int): Fragment {
                return fragments[position]
            }

            override fun getCount() = fragments.size

        }
        toolArea.setPageTransformer(false) { page, position ->
            page.translationX = page.width * -position
        }
        editToolsTab.setupWithViewPager(toolArea)

        val iconRes = listOf(R.drawable.ic_keyboard,
                R.drawable.ic_emotion_location_weather,
                R.drawable.ic_add_a_photo_white_24dp)
        (0 until iconRes.size).map { editToolsTab.getTabAt(it) }.forEachIndexed { i, it ->
            it?.customView = ImageView(context).apply { setImageDrawable(VectorDrawableCompat.create(resources, iconRes[i], null)) }
        }
        editToolsTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {
                when (tab.position) {
                    TAB_KEYBOARD_POS -> {
                        if (toolArea.isShown) {
                            hideInputMethod()
                        } else {
                            showInputMethod()
                        }
                    }
                    else -> {
                        if (toolArea.isShown) {
                            hideToolArea()
                        } else {
                            showToolArea()
                        }
                        hideInputMethod()
                    }
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    TAB_KEYBOARD_POS -> {
                        showInputMethod()
                    }
                    else -> {
                        hideInputMethod()
                        showToolArea()
                    }
                }
            }
        })

        setToolAreaHeight(mConfig.get(CONFIG_KEY_KEYBOARD_HEIGHT, context!!.dimen(R.dimen.editor_tool_area_init_height)))
    }

    private fun showToolArea() {
        toolArea.visibility = View.VISIBLE
        editToolsTab.setSelectedTabIndicatorColor(ResourcesCompat.getColor(resources, R.color.icon_color_primary, null))
    }

    private fun hideToolArea() {
        toolArea.visibility = View.GONE
        editToolsTab.setSelectedTabIndicatorColor(Color.TRANSPARENT)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults, object : PermissionUtil.OnRequestPermissionsResultCallbacks {
            override fun onPermissionsGranted(requestCode: Int, perms: List<String>, isAllGranted: Boolean) {
                if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
                    mPresenter.refreshLocation()
                    mPresenter.refreshWeather()
                }
            }

            override fun onPermissionsDenied(requestCode: Int, perms: List<String>, isAllDenied: Boolean) {
                if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
//                    mPresenter.start()
                }
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_share -> {
                mPresenter.shareContent()
                return true
            }
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.diary_detail_options, menu)
    }

    override fun setLoadingIndicator(active: Boolean) {
    }

    override fun hideDescription() {
        diaryContent.visibility = View.GONE
    }

    override fun showContent(content: String) {
        diaryContent.visibility = View.VISIBLE
        diaryContent.removeTextChangedListener(mWatcher)
        diaryContent.setText(content)
        diaryContent.addTextChangedListener(mWatcher)
//        diaryContent.setSelection(content.length)
        diaryContent.isCursorVisible = false

        formatEditContent()
    }

    private fun formatEditContent() {
        ThreadPools.postOnUI {
            logi("formatEditContent adjust paragraph", "detail")
            diaryContent.adjustParagraphSpace(diaryContent.dpToPx(editFontSize / 2))
            diaryContent.addPictureSpans()
            diaryContent.adjustCursorHeightNoException()
        }
    }

    override fun showDate(dateStr: String) {
        activity?.title = dateStr
    }

    override fun showDiaryDeleted() {
        activity?.finish()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        loge(data ?: "no", "select picture")

        if (requestCode == REQUEST_SELECT_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.data?.apply {
                    insertPictureMark(this) {
                        mPresenter.onContentChange(diaryContent.text.toString())
                        formatEditContent()
                    }
                } ?: context?.toast("insert picture error")
            }
        }
    }

    private fun insertPictureMark(uri: Uri, onEnd: (() -> Unit)) {
        ThreadPools.postOnQuene {
            generateImage(uri)?.apply {
                val key = generateKey(uri)
                val name = "${getImageDir()}/$key"
                mConfig.put(key, name)
                val out = FileOutputStream(name)
                compress(Bitmap.CompressFormat.PNG, 100, out)
                ThreadPools.postOnUI {
                    val append = SpannableStringBuilder("\n[]($key)\n")
                    diaryContent.text.also {
                        val selectStart = Selection.getSelectionStart(it)
                        val selectEnd = Selection.getSelectionEnd(it)
                        if (selectStart == -1) {
                            it.append(append)
                        } else if (selectEnd == selectStart) {
                            it.insert(selectStart, append)
                        } else {
                            it.replace(selectStart, selectEnd, append)
                        }
                    }
                    onEnd()
                }
            }
        }


    }

    private fun generateKey(uri: Uri): String {
        return uri.hashCode().toString()
    }

    private fun getImageDir(): String {
        val imagesDir = "${context!!.filesDir}/images".run { File(this) }
        if (!imagesDir.isDirectory) {
            imagesDir.mkdir()
        }
        return imagesDir.toString()
    }

    private fun EditText.addPictureSpan(start: Int, end: Int, key: String) {
        val file = mConfig.get(key, "")
        loadImage(file)?.run {
            BitmapDrawable(context.resources, this).also { drawable ->
                val width = diaryContent.width - diaryContent.paddingLeft - diaryContent.paddingRight
                val height = drawable.intrinsicHeight / drawable.intrinsicWidth.toFloat() * width
                drawable.setBounds(0, 0, width, height.toInt()).also {
                    loge("bound = ${drawable.bounds} w = ${drawable.bounds.width()} h = ${drawable.bounds.height()}")
                }
            }
        }?.apply {
            val imageSpan = ImageSpan(this)
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View?) {
                    context?.toast("hello world")
                }

                override fun updateDrawState(ds: TextPaint?) {
                    ds?.bgColor = Color.CYAN
                }
            }
            loge("span text = ${text.subSequence(start, end + 1)}")
            if (text.getSpans(start, end + 1, ImageSpan::class.java).isEmpty()) {
                text.setSpan(imageSpan, start, end + 1, SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE)
                text.setSpan(clickableSpan, start, end + 1, SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE)
            }
        }
    }

    private fun loadImage(file: String): Bitmap? {
        return BitmapFactory.decodeFile(file)
    }

    private fun generateImage(data: Uri): Bitmap? {
        val context = context as Context
        val edgeWidth = context.dip(10)
        val space = context.dip(10)

        val targetWidth = (diaryContent.width - diaryContent.paddingLeft - diaryContent.paddingRight - 2 * space - 2 * edgeWidth) / 2
        val bitmap = context.getScaleImage(data, targetWidth)?.let { ori ->
            val w = ori.width
            val edgeWidth = (edgeWidth.toFloat() * w / targetWidth).toInt()
            val space = (space.toFloat() * w / targetWidth).toInt()
            val h = ori.height
            Bitmap.createBitmap(w + 2 * edgeWidth + 2 * space, h + 2 * edgeWidth + 2 * space, Bitmap.Config.ARGB_4444).also {
                Canvas(it).run {
                    drawColor(Color.TRANSPARENT)
                    drawRect(space.toFloat(), space.toFloat(), (space + 2 * edgeWidth + w).toFloat(), (space + 2 * edgeWidth + h).toFloat(), Paint().also { it.color = Color.WHITE })
                    drawBitmap(ori, space + edgeWidth.toFloat(), space + edgeWidth.toFloat(), null)
                }
            }
        }
        return bitmap
    }

    override fun showMissingDiary() {
        loge("no this task")
        onBackPressed()
    }

    companion object {

        private const val REQUEST_CODE_LOCATION_PERMISSION = 0x64

        const val REQUEST_SELECT_PICTURE = 1

        private const val TAB_KEYBOARD_POS = 0

        private const val CONFIG_KEY_KEYBOARD_HEIGHT = "key_keyboard_height"

        const val KEY_DIARY_ID = "diaryId"


        fun newInstance(arguments: Bundle): DiaryDetailFragment {
            val fragment = DiaryDetailFragment()
            fragment.arguments = arguments
            return fragment
        }
    }

    override fun showLocation(location: LocationInfo) {
        statusFragment.showLocation(location)
    }

    // todo weatherIconUrl what?
    override fun showWeather(weather: String, weatherIconUrl: String) {
        statusFragment.showWeather(weather, weatherIconUrl)
    }

    override fun showEmotion(id: Long) {
        statusFragment.showEmotion(id)
    }

    override fun showDiarySaved() {
        Snackbar.make(view!!, getString(R.string.successfully_saved_diary_message), Snackbar.LENGTH_LONG).show()
    }

    override fun showEmptyDiaryError() {

    }

    override fun showInputMethod() {
        diaryContent.showInputMethod()
    }

    override fun hideInputMethod() {
        diaryContent.hideInputMethod()
    }

    override fun hasLocationPermission(): Boolean {
        return activity?.let {
            PermissionUtil.getLocationPermissions(it, DiaryDetailFragment.REQUEST_CODE_LOCATION_PERMISSION)
        } ?: false
    }

    override fun shareContent(content: String) {
        context?.let { ctx: Context ->
            startActivity(shareContentIntent(ctx, content))
        }
    }

    fun onBackPressed(): Boolean {
        if (editToolsTab.selectedTabPosition > 0 && toolArea.isShown) {
            hideToolArea()
            return true
        } else {
            return false
        }
    }

    fun EditText.addPictureSpans() {
        val reg = "\\[(.*)]\\((.*)\\)".toRegex()
        reg.findAll(text).forEach {
            //            addPictureSpan(0, 43, Uri.parse(it.groupValues[2]))
            addPictureSpan(it.range.start, it.range.endInclusive, it.groupValues[2])
        }
    }
}

abstract class MFragment : Fragment() {
    lateinit var mPresenter: DiaryDetailPresenter
    lateinit var mReporter: Reporter
    lateinit var mParentFragment: DiaryDetailFragment
}

private fun makeFragmentName(viewId: Int, id: Long): String {
    return "android:switcher:$viewId:$id"
}






