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
import android.view.*
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.empty.jinux.baselibaray.utils.dayStartTime
import com.empty.jinux.baselibaray.utils.hideInputMethod
import com.empty.jinux.baselibaray.utils.weekStartTime
import com.empty.jinux.baselibaray.view.recycleview.Item
import com.empty.jinux.baselibaray.view.recycleview.ItemAdapter
import com.empty.jinux.baselibaray.view.recycleview.SpaceItem
import com.empty.jinux.baselibaray.view.recycleview.withItems
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.databinding.FragmentDiaryListBinding
import com.empty.jinux.simplediary.report.Reporter
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailActivity
import com.empty.jinux.simplediary.ui.main.BackPressPrecessor
import com.empty.jinux.simplediary.ui.main.MainActivity
import com.empty.jinux.simplediary.ui.main.diarylist.adapter.CategoryEndItem
import com.empty.jinux.simplediary.ui.main.diarylist.adapter.CategoryItem
import com.empty.jinux.simplediary.ui.main.diarylist.adapter.DiaryItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Display a grid of [Diary]s. User can choose to view all, active or completed diaries.
 */
@AndroidEntryPoint
class DiaryListFragment : Fragment(), DiaryListContract.View, BackPressPrecessor {

    override fun onBackPress(): Boolean {
        searchView?.apply {
            if (this.isIconified) {
                return false
            }
            this.isIconified = true
            this.isIconified = true
            return true
        }
        return false
    }

    @Inject
    internal lateinit var mPresenter: DiaryListContract.Presenter

    @Inject
    internal lateinit var mReporter: Reporter

    override val isActive: Boolean
        get() = isAdded

    private lateinit var _binding: FragmentDiaryListBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryListBinding.inflate(inflater, container, false)
        return _binding.root
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
        _binding.diaryRecyclerView.withItems(mDiariesItems)
    }

    private fun setupNoDiaryView() {
        _binding.noDiaries.setOnClickListener {
            mPresenter.addNewDiary()
            mReporter.reportClick("no diary icon")
        }
    }

    private fun setupRefreshView() {
        (requireActivity()).let { activity ->
            // Set up progress indicator
            _binding.refreshLayout.setColorSchemeColors(
                ContextCompat.getColor(activity, R.color.colorPrimary),
                ContextCompat.getColor(activity, R.color.colorAccent),
                ContextCompat.getColor(activity, R.color.colorPrimaryDark)
            )
            // Set the scrolling view in the custom SwipeRefreshLayout.
            _binding.refreshLayout.setScrollUpChild(_binding.diaryRecyclerView)
            _binding.refreshLayout.setOnRefreshListener {
                mPresenter.loadDiaries(true)
            }
        }
    }

    private fun setupFloatButton() {
        activity?.findViewById<FloatingActionButton>(R.id.fab_add_diary)?.apply {
            isVisible = true
            setImageResource(R.drawable.ic_add)
            setOnClickListener {
                mPresenter.addNewDiary()
                mReporter.reportClick("add diary")
            }
            setOnLongClickListener {
                _binding.diaryRecyclerView.smoothScrollToPosition(_binding.diaryRecyclerView.adapter!!.itemCount)
                true
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
                searchManager.getSearchableInfo(it.componentName)
            )

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
        val srl = _binding.refreshLayout

        // Make sure setRefreshing() is called after the layout is done with everything else.
        srl.post { srl.isRefreshing = active }
    }

    override fun showDiaries(diaries: List<Diary>) {
        _binding.diaryRecyclerView.refreshFromDiariesList(diaries, object : DiaryItem.OnItemListener {
            override fun onItemClick(diary: Diary) {
                mPresenter.openDiaryDetails(diary)
                mReporter.reportClick("open diary")
            }

            override fun onDeleteClick(diary: Diary) {
                mPresenter.deleteDiary(diary)
                mReporter.reportClick("delete diary")
            }
        })

        _binding.diaryRecyclerView.visibility = View.VISIBLE
        _binding.noDiaries.visibility = View.GONE

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

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.onDestory()
    }

    private fun showNoDiariesViews(mainText: String, iconRes: Int) {
        _binding.diaryRecyclerView.visibility = View.GONE
        _binding.noDiaries.visibility = View.VISIBLE

        _binding.noDiariesMessage.text = mainText
        _binding.noDiariesIcon.setImageDrawable(VectorDrawableCompat.create(resources, iconRes, null))
    }

    override fun showAddDiary(todayWords: Int) {
        val intent = Intent(context, DiaryDetailActivity::class.java).apply {
            putExtra(DiaryDetailActivity.EXTRA_TODAY_WORD_COUNT, todayWords)
        }

        startActivityForResult(
            intent,
            MainActivity.REQUEST_ADD_DIARY
        )
    }

    override fun showDiaryDetailsUI(diaryId: Long, todayWords: Int) {
        val intent = Intent(context, DiaryDetailActivity::class.java).apply {
            putExtra(
                DiaryDetailActivity.EXTRA_DIARY_ID, diaryId,
            )
            putExtra(DiaryDetailActivity.EXTRA_TODAY_WORD_COUNT, todayWords)
        }

        startActivityForResult(
            intent,
            MainActivity.REQUEST_ADD_DIARY
        )
    }

    override fun showLoadingDiariesError() {
        showMessage(getString(R.string.loading_diaries_error))
    }

    private fun showMessage(message: String) {
        com.google.android.material.snackbar.Snackbar.make(
            view!!,
            message,
            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
        ).show()
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

private fun androidx.recyclerview.widget.RecyclerView.refreshFromDiariesList(
    diaries: List<Diary>,
    itemListener: DiaryItem.OnItemListener
) {
    val items = mutableListOf<Item>()

    var preWeekStart = 0L
    var preDay = 0L
    diaries.forEachIndexed { i, it ->
        val createdTime = it.meta.createdTime
        if (createdTime.weekStartTime() != preWeekStart) {
            preWeekStart = createdTime.weekStartTime()
            items.add(CategoryItem(preWeekStart))

        }
        val differentDay = createdTime.dayStartTime() != preDay
        if (differentDay) {
            preDay = createdTime.dayStartTime()
        }
        items.add(DiaryItem(it, differentDay, itemListener))

        val nextDiaryWeekStart =
            if (i < diaries.size - 1) diaries[i + 1].meta.createdTime.weekStartTime() else -1
        if (createdTime.weekStartTime() != nextDiaryWeekStart) {
            items.add(CategoryEndItem())
        }
    }

    val spaceHeight =
        context.resources.getDimension(R.dimen.diary_list_ending_space_size).roundToInt()
    items.add(0, SpaceItem(spaceHeight / 2))
    items.add(SpaceItem(spaceHeight))

    val itemAdapter = adapter as ItemAdapter
    itemAdapter.clear()
    itemAdapter.addAll(items)
    itemAdapter.notifyDataSetChanged()
}
