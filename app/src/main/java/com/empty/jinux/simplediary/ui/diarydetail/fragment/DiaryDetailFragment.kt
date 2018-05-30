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
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.res.ResourcesCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.ImageView
import com.empty.jinux.baselibaray.loge
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.INVALID_DIARY_ID
import com.empty.jinux.simplediary.data.LocationInfo
import com.empty.jinux.simplediary.report.Reporter
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailContract
import com.empty.jinux.simplediary.ui.diarydetail.fragment.edittools.KeyboardFragment
import com.empty.jinux.simplediary.ui.diarydetail.fragment.edittools.StatusFragment
import com.empty.jinux.simplediary.ui.diarydetail.presenter.DiaryDetailPresenter
import com.empty.jinux.simplediary.util.*
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.activity_demo.*
import kotlinx.android.synthetic.main.layout_diary_detail_edit_tool.*
import kotlinx.android.synthetic.main.taskdetail_frag.*
import org.jetbrains.anko.dimen
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

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        val taskId = arguments?.getLong(ARGUMENT_TASK_ID, INVALID_DIARY_ID) ?: INVALID_DIARY_ID
        mPresenter.setDiaryId(taskId)
    }

    override val isActive: Boolean
        get() = isAdded

    override fun onPause() {
        super.onPause()
        keyboardHeightListener.close()
        mPresenter.stop()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.taskdetail_frag, container, false)
    }

    private var mWatcher: TextWatcher? = null

    lateinit var keyboardHeightListener: KeyboardHeightProvider

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)

        keyboardHeightListener = KeyboardHeightProvider(activity!!)

        mWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (editor == null) return
            }

            override fun afterTextChanged(s: Editable?) {
                mPresenter.onContentChange(s.toString())
                diaryContent.adjustParagraphSpace()
                ThreadPools.postOnUI {
                    adjustScrollPosition()
                }
            }
        }
        diaryContent.addTextChangedListener(mWatcher)
        diaryContent.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_UP -> {
                    diaryContent.isLongClickable = true
                }
                MotionEvent.ACTION_MOVE -> diaryContent.isLongClickable = false
            }
            false
        }

        editContainer.setOnClickListener {
            diaryContent.apply { setSelection(text.length) }
            showInputMethod()
        }
        fragmentContainer.setOnClickListener {
            diaryContent.apply { setSelection(text.length) }
            showInputMethod()
        }

        initEditToolbar()

        keyboardHeightListener.observer = object : KeyboardHeightObserver {
            override fun onKeyboardHeightChanged(height: Int, orientation: Int) {
                if (diaryContent == null) {
                    return
                }

                val inputMethodShowed = height != 0
                if (inputMethodShowed) {
                    onInputMedhodShowed(height)
                } else {
                    onInputMedhodHided()
                }
            }
        }

        activity?.let {
            if (PermissionUtil.getLocationPermissions(it, REQUEST_CODE_LOCATION_PERMISSION)) {
                mPresenter.start()
            }
        }

    }

    override fun onResume() {
        super.onResume()

        ThreadPools.postOnUI {
            keyboardHeightListener.start()
        }
    }

    private fun onInputMedhodHided() {
        if (editToolsTab.selectedTabPosition == 0) {
            hideToolArea()
        }
    }

    private fun onInputMedhodShowed(height: Int) {
        diaryContent.isCursorVisible = true

        bottomSpace.layoutHeight = height + toolArea.dimen(R.dimen.diary_detail_edit_tool_height)
        toolArea.setCurrentItem(0, false)
        toolArea.layoutHeight = height
        showToolArea()

        ThreadPools.postOnUI {
            adjustScrollPosition()
        }
    }

    private fun adjustScrollPosition() {

        val editor = diaryContent
        val scrollView = scrollContainer

        val cursorLine = editor.getLineForCursor()
        val cursorLineBottom = editor.layout.getLineBottom(cursorLine)

        val cursorYOffset = cursorLineBottom - scrollView.scrollY
        val editorVisibleAreaheight = editTabContainer.top - 50

        if (cursorYOffset > editorVisibleAreaheight) {
            val scroll = cursorYOffset - editorVisibleAreaheight
            scrollView.smoothScrollBy(0, scroll)
        }
    }

    private lateinit var keyboardFragment: KeyboardFragment

    private lateinit var statusFragment: StatusFragment

    private fun initEditToolbar() {
        keyboardFragment = ((fragmentManager?.findFragmentByTag(makeFragmentName(toolArea.id, 0)) as? KeyboardFragment)
                ?: KeyboardFragment())
        statusFragment = ((fragmentManager?.findFragmentByTag(makeFragmentName(toolArea.id, 1)) as? StatusFragment)
                ?: StatusFragment())

        val fragments = listOf(keyboardFragment, statusFragment)
        fragments.forEach {
            it.mPresenter = mPresenter
            it.mReporter = mReporter
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
                R.drawable.ic_emotion)
        (0 until iconRes.size).map { editToolsTab.getTabAt(it) }.forEachIndexed { i, it ->
            it?.customView = ImageView(context).apply { setImageDrawable(resources.getDrawable(iconRes[i])) }
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
    }

    private fun showToolArea() {
        toolArea.visibility = View.VISIBLE
        editToolsTab.setSelectedTabIndicatorColor(ResourcesCompat.getColor(resources, R.color.colorAccent, null))
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
                    mPresenter.start()
                }
            }

            override fun onPermissionsDenied(requestCode: Int, perms: List<String>, isAllDenied: Boolean) {
                if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
//                    mPresenter.start()
                }
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_delete -> {
                mPresenter.deleteDiary()
                return true
            }
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.taskdetail_fragment_menu, menu)
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
        ThreadPools.postOnUI {
            diaryContent.adjustParagraphSpace()
        }
    }

    override fun showDate(dateStr: String) {
        activity?.title = dateStr
    }

    override fun showDiaryDeleted() {
        activity?.finish()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_EDIT_TASK) {
            // If the task was edited successfully, go back to the list.
            if (resultCode == Activity.RESULT_OK) {
                activity?.finish()
            }
        }
    }

    override fun showMissingDiary() {
        loge("no this task")
    }

    companion object {

        private const val REQUEST_CODE_LOCATION_PERMISSION = 0x64

        private const val ARGUMENT_TASK_ID = "TASK_ID"

        private const val REQUEST_EDIT_TASK = 1

        private const val TAB_KEYBOARD_POS = 0


        fun newInstance(taskId: Long): DiaryDetailFragment {
            val arguments = Bundle()
            arguments.putLong(ARGUMENT_TASK_ID, taskId)
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
}

private fun makeFragmentName(viewId: Int, id: Long): String {
    return "android:switcher:$viewId:$id"
}





