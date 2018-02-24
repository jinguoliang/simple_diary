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

package com.empty.jinux.simplediary.taskdetail

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.Toast
import com.empty.jinux.baselibaray.loge
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.addeditdiary.AddEditDiaryActivity
import com.empty.jinux.simplediary.addeditdiary.AddEditDiaryFragment
import com.google.android.gms.location.LocationServices
import com.google.common.base.Preconditions.checkNotNull
import kotlinx.android.synthetic.main.taskdetail_frag.*

/**
 * Main UI for the task detail screen.
 */
class TaskDetailFragment : Fragment(), TaskDetailContract.View {

    private var mPresenter: TaskDetailContract.Presenter? = null

    override val isActive: Boolean
        get() = isAdded

    override fun onResume() {
        super.onResume()
        mPresenter!!.start()
    }

    private val MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 0x25

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater!!.inflate(R.layout.taskdetail_frag, container, false)
        setHasOptionsMenu(true)

        // Set up floating action button
        (activity.findViewById<FloatingActionButton>(R.id.fab_edit_task)).setOnClickListener { mPresenter!!.editTask() }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        refreshLocation.setOnClickListener {
            mPresenter?.refreshLocation()
        }
    }

    override fun setPresenter(presenter: TaskDetailContract.Presenter) {
        mPresenter = checkNotNull(presenter)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_delete -> {
                mPresenter!!.deleteTask()
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

    override fun showDescription(description: String) {
        diaryContent.visibility = View.VISIBLE
        diaryContent.setText(description)
    }

    override fun showDate(dateStr: String) {
        date.text = dateStr
    }

    override fun showEditTask(taskId: String) {
        val intent = Intent(context, AddEditDiaryActivity::class.java)
        intent.putExtra(AddEditDiaryFragment.ARGUMENT_EDIT_TASK_ID, taskId)
        startActivityForResult(intent, REQUEST_EDIT_TASK)
    }

    override fun showTaskDeleted() {
        activity.finish()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_EDIT_TASK) {
            // If the task was edited successfully, go back to the list.
            if (resultCode == Activity.RESULT_OK) {
                activity.finish()
            }
        }
    }

    override fun showMissingTask() {
        loge("no this task")
    }

    companion object {

        private val ARGUMENT_TASK_ID = "TASK_ID"

        private val REQUEST_EDIT_TASK = 1

        fun newInstance(taskId: String?): TaskDetailFragment {
            val arguments = Bundle()
            arguments.putString(ARGUMENT_TASK_ID, taskId)
            val fragment = TaskDetailFragment()
            fragment.arguments = arguments
            return fragment
        }
    }

    override fun showLocation(city: String) {
        locationName.text = city
    }

}
