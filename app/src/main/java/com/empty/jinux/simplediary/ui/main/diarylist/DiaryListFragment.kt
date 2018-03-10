/*
 * Copyright 2016, The Android Open Source Project
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

package com.empty.jinux.simplediary.ui.main.diarylist

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailActivity
import com.empty.jinux.simplediary.ui.main.MainActivity
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.tasks_frag.*
import org.jetbrains.anko.intentFor
import java.util.*
import javax.inject.Inject

/**
 * Display a grid of [Diary]s. User can choose to view all, active or completed diaries.
 */
class DiaryListFragment : DaggerFragment(), DiaryListContract.View {

    @Inject
    lateinit internal var mPresenter: DiaryListContract.Presenter

    lateinit private var mListAdapter: DiariesAdapter

    /**
     * Listener for clicks on tasks in the ListView.
     */
    private var mItemListener: DiariesAdapter.DiaryItemListener = object : DiariesAdapter.DiaryItemListener {
        override fun onClick(diary: Diary) {
            mPresenter.openDiaryDetails(diary)
        }

        override fun onCompleteClick(diary: Diary) {
        }

        override fun onActivateClick(diary: Diary) {
        }
    }

    override val isActive: Boolean
        get() = isAdded

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mListAdapter = DiariesAdapter(ArrayList(0), mItemListener)
    }

    override fun onResume() {
        super.onResume()
        mPresenter.start()
    }

    override fun onPause() {
        super.onPause()
        mPresenter.stop()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mPresenter.result(requestCode, resultCode)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tasks_frag, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Set up tasks view
        tasks_list.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        tasks_list.adapter = mListAdapter

        noTasks.setOnClickListener { showAddDiary() }

        // Set up floating action button
        activity?.findViewById<FloatingActionButton>(R.id.fab_add_task)?.apply {
            setImageResource(R.drawable.ic_add)
            setOnClickListener { mPresenter.addNewDiary() }
        }

        activity?.let { activity ->
            // Set up progress indicator
            refresh_layout.setColorSchemeColors(
                    ContextCompat.getColor(activity, R.color.colorPrimary),
                    ContextCompat.getColor(activity, R.color.colorAccent),
                    ContextCompat.getColor(activity, R.color.colorPrimaryDark)
            )
            // Set the scrolling view in the custom SwipeRefreshLayout.
            refresh_layout.setScrollUpChild(tasks_list)
            refresh_layout.setOnRefreshListener { mPresenter.loadDiaries(false) }
        }


        setHasOptionsMenu(true)
    }

    override fun setLoadingIndicator(active: Boolean) {

        if (view == null) {
            return
        }
        val srl = refresh_layout as ScrollChildSwipeRefreshLayout

        // Make sure setRefreshing() is called after the layout is done with everything else.
        srl.post { srl.isRefreshing = active }
    }

    override fun showDiaries(diaries: List<Diary>) {
        mListAdapter.replaceData(diaries)

        tasks_list.visibility = View.VISIBLE
        noTasks.visibility = View.GONE
    }

    override fun showNoDiaries() {
        showNoTasksViews(
                resources.getString(R.string.no_tasks_all),
                R.drawable.ic_assignment_turned_in_24dp,
                false
        )
    }

    override fun showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_task_message))
    }

    private fun showNoTasksViews(mainText: String, iconRes: Int, showAddView: Boolean) {
        tasks_list.visibility = View.GONE
        noTasks.visibility = View.VISIBLE

        noTasksMain.text = mainText
        noTasksIcon.setImageDrawable(resources.getDrawable(iconRes))
        noTasksAdd.visibility = if (showAddView) View.VISIBLE else View.GONE
    }

    override fun showAddDiary() {
        startActivityForResult(context?.intentFor<DiaryDetailActivity>(),
                MainActivity.REQUEST_ADD_DIARY)
    }

    override fun showDiaryDetailsUI(diaryId: Int) {
        startActivity(context?.intentFor<DiaryDetailActivity>(
                DiaryDetailActivity.EXTRA_TASK_ID to diaryId))
    }

    override fun showDiaryMarkedComplete() {
        showMessage(getString(R.string.task_marked_complete))
    }

    override fun showLoadingDiariesError() {
        showMessage(getString(R.string.loading_tasks_error))
    }

    private fun showMessage(message: String) {
        Snackbar.make(view!!, message, Snackbar.LENGTH_LONG).show()
    }

    companion object {

        fun newInstance(): DiaryListFragment {
            return DiaryListFragment()
        }
    }

}
