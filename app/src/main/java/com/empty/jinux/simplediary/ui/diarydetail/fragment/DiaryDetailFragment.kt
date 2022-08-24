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
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.empty.jinux.baselibaray.log.loge
import com.empty.jinux.baselibaray.log.logi
import com.empty.jinux.baselibaray.thread.ThreadPools
import com.empty.jinux.baselibaray.utils.*
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.config.ConfigManager
import com.empty.jinux.simplediary.data.INVALID_DIARY_ID
import com.empty.jinux.simplediary.data.LocationInfo
import com.empty.jinux.simplediary.databinding.FragmentTaskdetailBinding
import com.empty.jinux.simplediary.databinding.LayoutDiaryDetailEditToolBinding
import com.empty.jinux.simplediary.intent.shareContentIntent
import com.empty.jinux.simplediary.report.Reporter
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailActivity
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailContract
import com.empty.jinux.simplediary.ui.diarydetail.fragment.edittools.KeyboardFragment
import com.empty.jinux.simplediary.ui.diarydetail.fragment.edittools.StatusFragment
import com.empty.jinux.simplediary.ui.diarydetail.presenter.DiaryDetailPresenter
import com.empty.jinux.simplediary.util.PermissionUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.roundToInt


/**
 * Main UI for the task detail screen.
 */
@AndroidEntryPoint
class DiaryDetailFragment : Fragment(), DiaryDetailContract.View {

    @Inject
    internal
    lateinit var mPresenter: DiaryDetailPresenter

    @Inject
    internal
    lateinit var mReporter: Reporter

    @Inject
    internal
    lateinit var mConfig: ConfigManager

    override fun onAttach(context: Context) {
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

        binding.diaryContent.addTextChangedListener(mWatcher)

        ThreadPools.postOnUI {
            keyboardHeightListener.start()
        }
    }

    override fun onPause() {
        logi("detail fragment onPause", "detail")
        super.onPause()
        keyboardHeightListener.close()
        binding.diaryContent.removeTextChangedListener(mWatcher)
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
        mPresenter.onDestory()
    }

    private lateinit var binding: FragmentTaskdetailBinding
    private lateinit var editToolBinding: LayoutDiaryDetailEditToolBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaskdetailBinding.inflate(inflater, container, false)
        editToolBinding = binding.editTool
        return binding.root
    }

    private var mWatcher: TextWatcher? = null

    lateinit var keyboardHeightListener: KeyboardHeightProvider

    private var mShowedGoodView = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)

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
        binding.editContainer.setOnClickListener {
            binding.diaryContent.apply { setSelection(text!!.length) }
            showInputMethod()
        }
        binding.fragmentContainer.setOnClickListener {
            binding.diaryContent.apply { setSelection(text!!.length) }
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
        binding.diaryContent.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_UP -> {
                    binding.diaryContent.isLongClickable = true
                }
                MotionEvent.ACTION_MOVE -> binding.diaryContent.isLongClickable = false
            }
            false
        }

        binding.diaryContent.mScrollParent = binding.scrollContainer
    }

    private fun onInputMedhodHided() {
        if (editToolBinding.editToolsTab.selectedTabPosition == 0) {
            hideToolArea()
        }
    }

    override fun showGoodView(): Boolean {
        binding.goodView.visibility = View.VISIBLE
        return true
    }

    override fun setTodayGood(show: Boolean) {
//        val actionbarCheck = (context as Activity).action_check
//        actionbarCheck.visibility = if (show) View.VISIBLE else View.INVISIBLE
//        if (!show) {
//            binding.goodView.visibility = View.GONE
//        }
    }

    var mKeyboardHeightCached = 0

    private fun onInputMethodShowed(height: Int) {
        mKeyboardHeightCached = height
        Log.e("JIN", "keyboardHeight: $height")
        binding.diaryContent.isCursorVisible = true

        setGoodViewHeight(height)
        setToolAreaHeight(height)
        editToolBinding.toolArea.setCurrentItem(0, false)
        showToolArea()

        ThreadPools.postOnUI {
            binding.diaryContent.adjustScrollPosition(
                binding.scrollContainer,
                editToolBinding.editTabContainer.top - context!!.resources.getDimension(R.dimen.detail_diary_editor_bottom)
                    .roundToInt()
            )
        }
    }

    private fun setGoodViewHeight(keyboardHeight: Int) {
        binding.goodView.layoutHeight = binding.fragmentContainer.height - keyboardHeight
    }

    private fun setToolAreaHeight(height: Int) {
        Log.e("JIN", "toolHeight: $height")
        if (height > 0 && height != editToolBinding.toolArea.layoutHeight) {
            binding.bottomSpace.layoutHeight =
                height + editToolBinding.toolArea.resources.getDimension(R.dimen.diary_detail_edit_tool_height)
                    .roundToInt()
            editToolBinding.toolArea.layoutHeight = height + 300
        }
    }

    private lateinit var keyboardFragment: KeyboardFragment

    private lateinit var statusFragment: StatusFragment

    private fun initEditToolbar() {
        keyboardFragment = ((fragmentManager?.findFragmentByTag(
            makeFragmentName(
                editToolBinding.toolArea.id,
                0
            )
        ) as? KeyboardFragment)
            ?: KeyboardFragment())
        statusFragment = ((fragmentManager?.findFragmentByTag(
            makeFragmentName(
                editToolBinding.toolArea.id,
                1
            )
        ) as? StatusFragment)
            ?: StatusFragment())

        val fragments = listOf(keyboardFragment, statusFragment)
        fragments.forEach {
            it.mPresenter = mPresenter
            it.mReporter = mReporter
        }

        editToolBinding.toolArea.adapter = object :
            FragmentPagerAdapter(parentFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getItem(position: Int): androidx.fragment.app.Fragment {
                return fragments[position]
            }

            override fun getCount() = fragments.size

        }
        editToolBinding.toolArea.setPageTransformer(false) { page, position ->
            page.translationX = page.width * -position
        }
        editToolBinding.editToolsTab.setupWithViewPager(editToolBinding.toolArea)

        val iconRes = listOf(
            R.drawable.ic_keyboard,
            R.drawable.ic_emotion_location_weather
        )
        (0 until iconRes.size).map { editToolBinding.editToolsTab.getTabAt(it) }.forEachIndexed { i, it ->
            it?.customView = ImageView(context).apply {
                setImageDrawable(
                    VectorDrawableCompat.create(
                        resources,
                        iconRes[i],
                        null
                    )
                )
            }
        }
        editToolBinding.editToolsTab.addOnTabSelectedListener(object :
            com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab) {
                Log.e("JIN", "tabSelected: ${tab.position}")
                when (tab.position) {
                    TAB_KEYBOARD_POS -> {
                        if (editToolBinding.toolArea.isShown) {
                            hideInputMethod()
                        } else {
                            showInputMethod()
                        }
                    }
                    else -> {
                        if (editToolBinding.toolArea.isShown) {
                            hideToolArea()
                        } else {
                            showToolArea()
                        }
                        hideInputMethod()
                    }
                }

            }

            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab) {
            }

            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab) {
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

        setToolAreaHeight(
            mConfig.get(
                CONFIG_KEY_KEYBOARD_HEIGHT,
                context!!.resources.getDimension(R.dimen.editor_tool_area_init_height).roundToInt()
            )
        )
    }

    private fun showToolArea() {
        editToolBinding.toolArea.visibility = View.VISIBLE
        editToolBinding.editToolsTab.setSelectedTabIndicatorColor(
            ResourcesCompat.getColor(
                resources,
                R.color.icon_color_primary,
                null
            )
        )
    }

    private fun hideToolArea() {
        editToolBinding.toolArea.visibility = View.GONE
        editToolBinding.editToolsTab.setSelectedTabIndicatorColor(Color.TRANSPARENT)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtil.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            object : PermissionUtil.OnRequestPermissionsResultCallbacks {
                override fun onPermissionsGranted(
                    requestCode: Int,
                    perms: List<String>,
                    isAllGranted: Boolean
                ) {
                    if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
                        mPresenter.refreshLocation()
                        mPresenter.refreshWeather()
                    }
                }

                override fun onPermissionsDenied(
                    requestCode: Int,
                    perms: List<String>,
                    isAllDenied: Boolean
                ) {
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
        binding.diaryContent.visibility = View.GONE
    }

    override fun showContent(content: String) {
        binding.diaryContent.apply {
            visibility = View.VISIBLE
            removeTextChangedListener(mWatcher)
            setText(content)
            addTextChangedListener(mWatcher)
//        diaryContent.setSelection(content.length)
            isCursorVisible = false
        }


        formatEditContent()
    }

    private fun formatEditContent() {
        ThreadPools.postOnUI {
            logi("formatEditContent adjust paragraph", "detail")
            binding.diaryContent.adjustParagraphSpace(R.dimen.editor_paragraph_end)
            binding.diaryContent.adjustCursorHeightNoException()
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
        onBackPressed()
    }

    companion object {

        private const val REQUEST_CODE_LOCATION_PERMISSION = 0x64

        private const val REQUEST_EDIT_TASK = 1

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
        com.google.android.material.snackbar.Snackbar.make(
            view!!,
            getString(R.string.successfully_saved_diary_message),
            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
        ).show()
    }

    override fun showEmptyDiaryError() {

    }

    override fun showInputMethod() {
        binding.diaryContent.showInputMethod()
    }

    override fun hideInputMethod() {
        binding.diaryContent.hideInputMethod()
    }

    override fun hasLocationPermission(): Boolean {
        return activity?.let {
            PermissionUtil.getLocationPermissions(
                it,
                DiaryDetailFragment.REQUEST_CODE_LOCATION_PERMISSION
            )
        } ?: false
    }

    override fun shareContent(content: String) {
        context?.let { ctx: Context ->
            startActivity(shareContentIntent(ctx, content))
        }
    }

    fun onBackPressed(): Boolean {
        if (editToolBinding.editToolsTab.selectedTabPosition > 0 && editToolBinding.toolArea.isShown) {
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






