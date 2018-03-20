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
import com.empty.jinux.baselibaray.loge
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.INVALID_DIARY_ID
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailContract
import com.empty.jinux.simplediary.ui.diarydetail.presenter.DiaryDetailPresenter
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.taskdetail_frag.*
import javax.inject.Inject

/**
 * Main UI for the task detail screen.
 */
class DiaryDetailFragment : DaggerFragment(), DiaryDetailContract.View {

    @Inject internal
    lateinit var mPresenter: DiaryDetailPresenter

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        val taskId = arguments?.getLong(ARGUMENT_TASK_ID, INVALID_DIARY_ID) ?: INVALID_DIARY_ID
        mPresenter.setDiaryId(taskId)
    }

    override val isActive: Boolean
        get() = isAdded

    override fun onResume() {
        super.onResume()
        mPresenter.start()
    }

    override fun onPause() {
        super.onPause()
        mPresenter.stop()
    }

    private val MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 0x25

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.taskdetail_frag, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)

        refreshLocation.setOnClickListener {
            mPresenter.refreshLocation()
        }
        diaryContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                mPresenter.onContentChange(s.toString())
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
        date.text = dateStr
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
        locationName.text = city
    }

    override fun showWeather(weather: String, weatherIconUrl: String) {
        weatherName.text = weather
        Picasso.with(context).load(weatherIconUrl).into(weatherIcon)
    }

    override fun showDiarySaved() {
        Snackbar.make(view!!, getString(R.string.successfully_saved_diary_message), Snackbar.LENGTH_LONG).show()
    }

    override fun showEmptyDiaryError() {

    }

    override fun showInputMethod() {
        val im = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        diaryContent.postDelayed({
            im?.showSoftInput(diaryContent, InputMethodManager.SHOW_FORCED)
        }, 500)
    }

    override fun hideInputMethod() {
        val im = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        im?.hideSoftInputFromWindow(diaryContent.windowToken, 0)
    }


}
