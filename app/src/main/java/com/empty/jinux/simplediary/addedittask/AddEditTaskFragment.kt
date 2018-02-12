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

package com.empty.jinux.simplediary.addedittask

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.empty.jinux.simplediary.R
import com.google.common.base.Preconditions.checkNotNull
import javax.inject.Inject

/**
 * Main UI for the add task screen. Users can enter a task title and description.
 */
class AddEditTaskFragment : Fragment(), AddEditTaskContract.View {

    private var mPresenter: AddEditTaskContract.Presenter? = null

    private var mTitle: TextView? = null

    private var mDescription: TextView? = null

    override val isActive: Boolean
        get() = isAdded

    override fun onResume() {
        super.onResume()
        mPresenter!!.start()
    }

    override fun setPresenter(presenter: AddEditTaskContract.Presenter) {
        mPresenter = checkNotNull(presenter)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        fab_edit_task_done.setImageResource(R.drawable.ic_done)
//        fab_edit_task_done.setOnClickListener(View.OnClickListener { mPresenter!!.saveTask(mTitle!!.text.toString(), mDescription!!.text.toString()) })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater!!.inflate(R.layout.addtask_frag, container, false)
        mTitle = root.findViewById(R.id.add_task_title)
        mDescription = root.findViewById(R.id.add_task_description)

        setHasOptionsMenu(true)
        retainInstance = true
        return root
    }

    override fun showEmptyTaskError() {
        Toast.makeText(context, getString(R.string.empty_task_message), Toast.LENGTH_LONG).show()
    }

    override fun showTasksList() {
        activity?.setResult(Activity.RESULT_OK)
        activity?.finish()
    }

    override fun setTitle(title: String) {
        mTitle!!.text = title
    }

    override fun setDescription(description: String) {
        mDescription!!.text = description
    }

    companion object {

        val ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID"

        fun newInstance(): AddEditTaskFragment {
            return AddEditTaskFragment()
        }
    }
}// Required empty public constructor
