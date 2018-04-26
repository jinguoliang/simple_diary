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
import com.empty.jinux.simplediary.report.Reporter
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailActivity
import com.empty.jinux.simplediary.ui.main.MainActivity
import com.empty.jinux.simplediary.ui.main.diarylist.adapter.DiariesRecyclerViewWithCategoriesAdapter
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.diaries_frag.*
import org.jetbrains.anko.intentFor
import java.util.*
import javax.inject.Inject

/**
 * Display a grid of [Diary]s. User can choose to view all, active or completed diaries.
 */
class DiaryListFragment : DaggerFragment(), DiaryListContract.View {

    @Inject
    internal lateinit var mPresenter: DiaryListContract.Presenter

    @Inject
    internal lateinit var mReporter: Reporter

    private lateinit var mDiariesAdapter: DiariesRecyclerViewWithCategoriesAdapter

    /**
     * Listener for clicks on diaries in the ListView.
     */
    private var mItemListener: DiariesRecyclerViewWithCategoriesAdapter.DiaryItemListener = object : DiariesRecyclerViewWithCategoriesAdapter.DiaryItemListener {
        override fun onClick(diary: Diary) {
            mPresenter.openDiaryDetails(diary)
            mReporter.reportClick("open diary")
        }

        override fun onDeleteClick(diary: Diary) {
            mPresenter.deleteDiary(diary)
            mReporter.reportClick("delete diary")
        }
    }

    override val isActive: Boolean
        get() = isAdded

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDiariesAdapter = DiariesRecyclerViewWithCategoriesAdapter(ArrayList(0), mItemListener)
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
        return inflater.inflate(R.layout.diaries_frag, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Set up diaries view
        diaryRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        diaryRecyclerView.adapter = mDiariesAdapter

        noDiaries.setOnClickListener { showAddDiary() }

        // Set up floating action button
        activity?.findViewById<FloatingActionButton>(R.id.fab_add_diary)?.apply {
            visibility = View.VISIBLE
            setImageResource(R.drawable.ic_add)
            setOnClickListener {
                mPresenter.addNewDiary()
                mReporter.reportClick("add diary")
            }
        }

        activity?.let { activity ->
            // Set up progress indicator
            refresh_layout.setColorSchemeColors(
                    ContextCompat.getColor(activity, R.color.colorPrimary),
                    ContextCompat.getColor(activity, R.color.colorAccent),
                    ContextCompat.getColor(activity, R.color.colorPrimaryDark)
            )
            // Set the scrolling view in the custom SwipeRefreshLayout.
            refresh_layout.setScrollUpChild(diaryRecyclerView)
            refresh_layout.setOnRefreshListener { mPresenter.loadDiaries(false) }
        }
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
        mDiariesAdapter.replaceData(diaries)

        diaryRecyclerView.visibility = View.VISIBLE
        noDiaries.visibility = View.GONE
    }

    override fun showNoDiaries() {
        showNoDiariesViews(
                resources.getString(R.string.no_diaries_all),
                R.drawable.ic_assignment_turned_in_24dp
        )
    }

    override fun showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_diary_message))
    }

    private fun showNoDiariesViews(mainText: String, iconRes: Int) {
        diaryRecyclerView.visibility = View.GONE
        noDiaries.visibility = View.VISIBLE

        noDiariesMessage.text = mainText
        noDiariesIcon.setImageDrawable(resources.getDrawable(iconRes))
    }

    override fun showAddDiary() {
        startActivityForResult(context?.intentFor<DiaryDetailActivity>(),
                MainActivity.REQUEST_ADD_DIARY)
    }

    override fun showDiaryDetailsUI(diaryId: Long) {
        startActivity(context?.intentFor<DiaryDetailActivity>(
                DiaryDetailActivity.EXTRA_DIARY_ID to diaryId))
    }

    override fun showLoadingDiariesError() {
        showMessage(getString(R.string.loading_diaries_error))
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
