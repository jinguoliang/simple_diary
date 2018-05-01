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
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import com.empty.jinux.baselibaray.loge
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.INVALID_DIARY_ID
import com.empty.jinux.simplediary.report.Reporter
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailContract
import com.empty.jinux.simplediary.ui.diarydetail.presenter.DiaryDetailPresenter
import com.empty.jinux.simplediary.util.PermissionUtil
import com.empty.jinux.simplediary.util.getScreenHeight
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.layout_diary_detail_edit_tool.*
import kotlinx.android.synthetic.main.taskdetail_frag.*
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
        mPresenter.stop()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.taskdetail_frag, container, false)
    }

    private val REQUEST_CODE_LOCATION_PERMISSION = 0x64

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)

        diaryContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                mPresenter.onContentChange(s.toString())
            }

        })
        activity?.let {
            if (PermissionUtil.getLocationPermissions(it, REQUEST_CODE_LOCATION_PERMISSION)) {
                mPresenter.start()
            }
        }

        initEditToolbar()
    }

    private fun initEditToolbar() {
        toolInputMethod.setOnClickListener {
            toggleInputMethod()
            mReporter.reportClick("detail_tool_toggle")
        }

        toolLocation.setOnClickListener {
            mPresenter.refreshLocation()
            mReporter.reportClick("detail_tool_location")
        }

        toolWeather.adapter = SpinnerDrawableAdapter(context,
                R.layout.spinner_emotion_item,
                R.layout.drop_down_emotion_item,
                MyWeatherIcons.getAllMyIcon())
        toolWeather.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                mReporter.reportClick("detail_tool_weather", "no")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mPresenter.setWeather(MyWeatherIcons.getIconByIndex(position))
                mReporter.reportClick("detail_tool_weather", MyWeatherIcons.getWeatherName(position))
            }

        }

        toolEmotion.adapter = SpinnerDrawableAdapter(context,
                R.layout.spinner_emotion_item,
                R.layout.drop_down_emotion_item,
                MyEmotionIcons.getAllMyIcon())
        toolEmotion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                mReporter.reportClick("detail_tool_emotion", "no")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mPresenter.setEmotion(position.toLong())
                mReporter.reportClick("detail_tool_emotion", MyEmotionIcons.getEmotionName(position))
            }

        }
    }

    private fun toggleInputMethod() {
        if (isInputMethodShowed()) {
            hideInputMethod()
        } else {
            showInputMethod()
        }
    }

    private fun isInputMethodShowed(): Boolean {
        return activity?.run {
            view!!.height < getScreenHeight() * 3 / 4
        } ?: false
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
        diaryContent.setText(content)
        diaryContent.setSelection(content.length)
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

        private val ARGUMENT_TASK_ID = "TASK_ID"

        private val REQUEST_EDIT_TASK = 1

        fun newInstance(taskId: Long): DiaryDetailFragment {
            val arguments = Bundle()
            arguments.putLong(ARGUMENT_TASK_ID, taskId)
            val fragment = DiaryDetailFragment()
            fragment.arguments = arguments
            return fragment
        }
    }

    override fun showLocation(city: String) {
        Snackbar.make(view!!, city, Snackbar.LENGTH_LONG).apply {
            setAction(R.string.ok) {
                dismiss()
            }
            show()
        }
    }

    override fun showWeather(weather: String, icon: String) {
        toolWeather.setSelection(MyWeatherIcons.getIconIndex(icon))
    }

    override fun showEmotion(id: Long) {
        toolEmotion.setSelection(id.toInt())
    }

    override fun showDiarySaved() {
        Snackbar.make(view!!, getString(R.string.successfully_saved_diary_message), Snackbar.LENGTH_LONG).show()
    }

    override fun showEmptyDiaryError() {

    }

    override fun showInputMethod() {
        val im = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        im?.showSoftInput(diaryContent, InputMethodManager.SHOW_FORCED)
    }

    override fun hideInputMethod() {
        val im = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        im?.hideSoftInputFromWindow(diaryContent.windowToken, 0)
    }


}

