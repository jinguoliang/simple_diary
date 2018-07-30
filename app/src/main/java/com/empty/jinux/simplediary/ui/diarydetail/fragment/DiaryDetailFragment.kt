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
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.res.ResourcesCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.empty.jinux.baselibaray.log.loge
import com.empty.jinux.baselibaray.log.logi
import com.empty.jinux.baselibaray.thread.ThreadPools
import com.empty.jinux.baselibaray.utils.TextWatcherAdapter
import com.empty.jinux.baselibaray.utils.dpToPx
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
import org.jetbrains.anko.toast
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

    private lateinit var mEditorFormator: EditorFormator

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)

        mEditorFormator = EditorFormator(diaryContent, mConfig)

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
                mEditorFormator.formatEditContent(editFontSize)
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

        diaryContent.movementMethod = MArrowKeyMethod()

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

        mEditorFormator.formatEditContent(editFontSize)
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
                    mEditorFormator.insertPictureMark(this) {
                        mPresenter.onContentChange(diaryContent.text.toString())
                        mEditorFormator.formatEditContent(editFontSize)
                    }
                } ?: context?.toast("insert picture error")
            }
        }
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
}

abstract class MFragment : Fragment() {
    lateinit var mPresenter: DiaryDetailPresenter
    lateinit var mReporter: Reporter
    lateinit var mParentFragment: DiaryDetailFragment
}

private fun makeFragmentName(viewId: Int, id: Long): String {
    return "android:switcher:$viewId:$id"
}