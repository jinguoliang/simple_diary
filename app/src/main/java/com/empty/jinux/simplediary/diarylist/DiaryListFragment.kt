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

package com.empty.jinux.simplediary.diarylist

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.CheckBox
import android.widget.TextView
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.addeditdiary.AddEditDiaryActivity
import com.empty.jinux.simplediary.data.Diary
import com.empty.jinux.simplediary.taskdetail.TaskDetailActivity
import com.google.common.base.Preconditions.checkNotNull
import com.google.common.collect.Lists
import kotlinx.android.synthetic.main.tasks_frag.*
import java.util.*

/**
 * Display a grid of [Diary]s. User can choose to view all, active or completed diaries.
 */
class DiaryListFragment : Fragment(), DiaryListContract.View {

    lateinit private var mPresenter: DiaryListContract.Presenter

    lateinit private var mListAdapter: DiariesAdapter

    /**
     * Listener for clicks on tasks in the ListView.
     */
    internal var mItemListener: DiaryItemListener = object : DiaryItemListener {
        override fun onClick(diary: Diary) {
            mPresenter.openTaskDetails(diary)
        }

        override fun onCompleteClick(diary: Diary) {
            mPresenter.completeTask(diary)
        }

        override fun onActivateClick(diary: Diary) {
            mPresenter.activateTask(diary)
        }
    }

    override val isActive: Boolean
        get() = isAdded

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mListAdapter = DiariesAdapter(ArrayList<Diary>(0), mItemListener)
    }

    override fun onResume() {
        super.onResume()
        mPresenter.start()
    }

    override fun onPause() {
        super.onPause()
        mPresenter.stop()
    }


    override fun setPresenter(presenter: DiaryListContract.Presenter) {
        mPresenter = checkNotNull(presenter)
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

        noTasks.setOnClickListener { showAddTask() }

        // Set up floating action button
        val fab = activity.findViewById<FloatingActionButton>(R.id.fab_add_task)

        fab.setImageResource(R.drawable.ic_add)
        fab.setOnClickListener { mPresenter.addNewTask() }

        // Set up progress indicator
        refresh_layout.setColorSchemeColors(
                ContextCompat.getColor(activity, R.color.colorPrimary),
                ContextCompat.getColor(activity, R.color.colorAccent),
                ContextCompat.getColor(activity, R.color.colorPrimaryDark)
        )
        // Set the scrolling view in the custom SwipeRefreshLayout.
        refresh_layout.setScrollUpChild(tasks_list)
        refresh_layout.setOnRefreshListener { mPresenter.loadTasks(false) }

        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clear -> mPresenter.clearCompletedTasks()
            R.id.menu_filter -> showFilteringPopUpMenu()
            R.id.menu_refresh -> mPresenter.loadTasks(true)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu)
    }

    override fun showFilteringPopUpMenu() {
        val popup = PopupMenu(context, activity.findViewById(R.id.menu_filter))
        popup.menuInflater.inflate(R.menu.filter_tasks, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.active -> mPresenter.filtering = DiaryListFilterType.ACTIVE
                R.id.completed -> mPresenter.filtering = DiaryListFilterType.COMPLETED
                else -> mPresenter.filtering = DiaryListFilterType.ALL
            }
            mPresenter.loadTasks(false)
            true
        }

        popup.show()
    }

    override fun setLoadingIndicator(active: Boolean) {

        if (view == null) {
            return
        }
        val srl = refresh_layout as ScrollChildSwipeRefreshLayout

        // Make sure setRefreshing() is called after the layout is done with everything else.
        srl.post { srl.isRefreshing = active }
    }

    override fun showTasks(tasks: List<Diary>) {
        mListAdapter.replaceData(tasks)

        tasks_list.visibility = View.VISIBLE
        noTasks.visibility = View.GONE
    }

    override fun showNoActiveTasks() {
        showNoTasksViews(
                resources.getString(R.string.no_tasks_active),
                R.drawable.ic_check_circle_24dp,
                false
        )
    }

    override fun showNoTasks() {
        showNoTasksViews(
                resources.getString(R.string.no_tasks_all),
                R.drawable.ic_assignment_turned_in_24dp,
                false
        )
    }

    override fun showNoCompletedTasks() {
        showNoTasksViews(
                resources.getString(R.string.no_tasks_completed),
                R.drawable.ic_verified_user_24dp,
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

    override fun showActiveFilterLabel() {
        filteringLabel.text = resources.getString(R.string.label_active)
    }

    override fun showCompletedFilterLabel() {
        filteringLabel.text = resources.getString(R.string.label_completed)
    }

    override fun showAllFilterLabel() {
        filteringLabel.text = resources.getString(R.string.label_all)
    }

    override fun showAddTask() {
        val intent = Intent(context, AddEditDiaryActivity::class.java)
        startActivityForResult(intent, AddEditDiaryActivity.REQUEST_ADD_TASK)
    }

    override fun showTaskDetailsUi(taskId: String) {
        // in it's own Activity, since it makes more sense that way and it gives us the flexibility
        // to show some Intent stubbing.
        val intent = Intent(context, TaskDetailActivity::class.java)
        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, taskId)
        startActivity(intent)
    }

    override fun showTaskMarkedComplete() {
        showMessage(getString(R.string.task_marked_complete))
    }

    override fun showTaskMarkedActive() {
        showMessage(getString(R.string.task_marked_active))
    }

    override fun showCompletedTasksCleared() {
        showMessage(getString(R.string.completed_tasks_cleared))
    }

    override fun showLoadingTasksError() {
        showMessage(getString(R.string.loading_tasks_error))
    }

    private fun showMessage(message: String) {
        Snackbar.make(view!!, message, Snackbar.LENGTH_LONG).show()
    }

    private class DiariesAdapter(diaries: List<Diary>, private val mItemListener: DiaryItemListener)
        : RecyclerView.Adapter<DiaryViewHolder>() {

        private var mDiaries: List<Diary> = Lists.newArrayList()

        init {
            setList(diaries)
        }

        fun replaceData(diaries: List<Diary>) {
            setList(diaries)
            notifyDataSetChanged()
        }

        private fun setList(diaries: List<Diary>) {
            mDiaries = checkNotNull(diaries)
        }

        override fun getItemCount(): Int {
            return mDiaries.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
            return DiaryViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.task_item, parent, false), mItemListener)
        }

        override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
            holder.bind(mDiaries[position])
        }
    }

    class DiaryViewHolder(v: View, val mItemListener: DiaryItemListener) : RecyclerView.ViewHolder(v) {
        private var titleTV: TextView = v.findViewById(R.id.title)
        private var completeCB: CheckBox = v.findViewById(R.id.complete)

        fun bind(diary: Diary): Unit {
            titleTV.text = diary.titleForList
            // Active/completed diary UI
            completeCB.isChecked = diary.isCompleted
            if (diary.isCompleted) {
                itemView.setBackgroundDrawable(itemView.context
                        .resources.getDrawable(R.drawable.list_completed_touch_feedback))
            } else {
                itemView.setBackgroundDrawable(itemView.context
                        .resources.getDrawable(R.drawable.touch_feedback))
            }

            completeCB.setOnClickListener {
                if (!diary.isCompleted) {
                    mItemListener.onCompleteClick(diary)
                } else {
                    mItemListener.onActivateClick(diary)
                }
            }

            itemView.setOnClickListener { mItemListener.onClick(diary) }
        }

    }

    interface DiaryItemListener {

        fun onClick(diary: Diary)

        fun onCompleteClick(diary: Diary)

        fun onActivateClick(diary: Diary)
    }

    companion object {

        fun newInstance(): DiaryListFragment {
            return DiaryListFragment()
        }
    }

}
