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
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.SearchView
import com.empty.jinux.baselibaray.utils.hideInputMethod
import com.empty.jinux.baselibaray.view.recycleview.Item
import com.empty.jinux.baselibaray.view.recycleview.ItemAdapter
import com.empty.jinux.baselibaray.view.recycleview.withItems
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.report.Reporter
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailActivity
import com.empty.jinux.simplediary.ui.main.MainActivity
import com.empty.jinux.simplediary.ui.main.diarylist.adapter.CategoryEndItem
import com.empty.jinux.simplediary.ui.main.diarylist.adapter.CategoryItem
import com.empty.jinux.simplediary.ui.main.diarylist.adapter.DiaryItem
import com.empty.jinux.simplediary.util.dayTime
import com.empty.jinux.simplediary.util.weekStartTime
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_diary_list.*
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.intentFor
import javax.inject.Inject

/**
 * Display a grid of [Diary]s. User can choose to view all, active or completed diaries.
 */
class DiaryListFragment : DaggerFragment(), DiaryListContract.View {

    @Inject
    internal lateinit var mPresenter: DiaryListContract.Presenter

    @Inject
    internal lateinit var mReporter: Reporter

    override val isActive: Boolean
        get() = isAdded

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_diary_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)

        setupDiariesView()
        setupNoDiaryView()
        setupFloatButton()
        setupRefreshView()
    }

    private val mDiariesItems = mutableListOf<Item>()

    private fun setupDiariesView() {
        diaryRecyclerView.withItems(mDiariesItems)
    }

    private fun setupNoDiaryView() {
        noDiaries.setOnClickListener {
            mPresenter.addNewDiary()
            mReporter.reportClick("no diary icon")
        }
    }

    private fun setupRefreshView() {
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

    private fun setupFloatButton() {
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
        searchView?.hideInputMethod()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mPresenter.result(requestCode, resultCode)
    }

    private var searchView: SearchView? = null

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_options, menu)
        setupSearchView(menu)
    }

    private fun setupSearchView(menu: Menu) {
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
        diaryRecyclerView.refreshFromDiariesList(diaries, object : DiaryItem.OnItemListener {
            override fun onItemClick(diary: Diary) {
                mPresenter.openDiaryDetails(diary)
                mReporter.reportClick("open diary")
            }

            override fun onDeleteClick(diary: Diary) {
                mPresenter.deleteDiary(diary)
                mReporter.reportClick("delete diary")
            }
        })

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

    override fun showAddDiary(todayWords: Int) {
        startActivityForResult(context?.intentFor<DiaryDetailActivity>(DiaryDetailActivity.EXTRA_TODAY_WORD_COUNT to todayWords),
                MainActivity.REQUEST_ADD_DIARY)
    }

    override fun showDiaryDetailsUI(diaryId: Long, todayWords: Int) {
        startActivity(context?.intentFor<DiaryDetailActivity>(
                DiaryDetailActivity.EXTRA_DIARY_ID to diaryId,
                DiaryDetailActivity.EXTRA_TODAY_WORD_COUNT to todayWords))
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

private fun RecyclerView.refreshFromDiariesList(diaries: List<Diary>, itemListener: DiaryItem.OnItemListener) {
    val items = mutableListOf<Item>()

    var preWeekStart = 0L
    var preDay = 0L
    diaries.forEachWithIndex { i, it ->
        val createdTime = it.meta.createdTime
        if (createdTime.weekStartTime() != preWeekStart) {
            preWeekStart = createdTime.weekStartTime()
            items.add(CategoryItem(preWeekStart))

        }
        val differentDay = createdTime.dayTime() != preDay
        if (differentDay) {
            preDay = createdTime.dayTime()
        }
        items.add(DiaryItem(it, differentDay, itemListener))

        val nextDiaryWeekStart = if (i < diaries.size - 1) diaries[i + 1].meta.createdTime.weekStartTime() else -1
        if (createdTime.weekStartTime() != nextDiaryWeekStart) {
            items.add(CategoryEndItem())
        }
    }

    val itemAdapter = adapter as ItemAdapter
    itemAdapter.clear()
    itemAdapter.addAll(items)
    itemAdapter.notifyDataSetChanged()
}
