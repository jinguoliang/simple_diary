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

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.SearchView
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.report.Reporter
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailActivity
import com.empty.jinux.simplediary.ui.main.MainActivity
import com.empty.jinux.simplediary.ui.main.diarylist.adapter.DiariesRecyclerViewWithCategoriesAdapter
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_diary_list.*
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_diary_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)

        setUpDiariesView()
        setUpNoDiaryView()
        setUpFloatButton()
        setUpRefreshView()
    }

    private fun setUpDiariesView() {
        mDiariesAdapter = DiariesRecyclerViewWithCategoriesAdapter(ArrayList(0), mItemListener)
        diaryRecyclerView.adapter = mDiariesAdapter
    }

    private fun setUpNoDiaryView() {
        noDiaries.setOnClickListener {
            mPresenter.addNewDiary()
            mReporter.reportClick("no diary icon")
        }
    }

    private fun setUpRefreshView() {
        activity?.let { activity ->
            // Set up progress indicator
            refresh_layout.setColorSchemeColors(
                    ContextCompat.getColor(activity, R.color.colorPrimary),
                    ContextCompat.getColor(activity, R.color.colorAccent),
                    ContextCompat.getColor(activity, R.color.colorPrimaryDark)
            )
            // Set the scrolling view in the custom SwipeRefreshLayout.
            refresh_layout.setScrollUpChild(diaryRecyclerView)
            refresh_layout.setOnRefreshListener { mPresenter.loadDiaries(true) }
        }
    }

    private fun setUpFloatButton() {
        activity?.findViewById<FloatingActionButton>(R.id.fab_add_diary)?.apply {
            visibility = View.VISIBLE
            setImageResource(R.drawable.ic_add)
            setOnClickListener {
                mPresenter.addNewDiary()
                mReporter.reportClick("add diary")
            }
        }
    }


    override fun onResume() {
        super.onResume()
        mPresenter.start()
    }

    override fun onPause() {
        super.onPause()
        mPresenter.stop()

        // wow the isIconified is not nice, it's just like onCloseClicked when pass true， so we need two times
        searchView?.isIconified = true
        searchView?.isIconified = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mPresenter.result(requestCode, resultCode)
    }

    private var searchView: SearchView? = null

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_options, menu)
        setUpSearchView(menu)
    }

    private fun setUpSearchView(menu: Menu) {
        activity?.let {
            val searchManager = it.getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchView = menu.findItem(R.id.search).actionView as SearchView
            searchView?.setSearchableInfo(
                    searchManager.getSearchableInfo(it.componentName))

            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    handleSearch(newText ?: "")
                    return true
                }
            })
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

        mReporter.reportCount("diaries all", diaries.size)
    }

    override fun showNoDiaries() {
        showNoDiariesViews(
                resources.getString(R.string.no_diaries_all),
                R.drawable.ic_no_diary
        )
    }

    override fun showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_diary_message))
    }

    private fun showNoDiariesViews(mainText: String, iconRes: Int) {
        diaryRecyclerView.visibility = View.GONE
        noDiaries.visibility = View.VISIBLE

        noDiariesMessage.text = mainText
        noDiariesIcon.setImageDrawable(VectorDrawableCompat.create(resources, iconRes, null))
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

    fun handleSearch(query: String) {
        mPresenter.searchDiary(query)
    }

    companion object {

        fun newInstance(): DiaryListFragment {
            return DiaryListFragment()
        }
    }

}
