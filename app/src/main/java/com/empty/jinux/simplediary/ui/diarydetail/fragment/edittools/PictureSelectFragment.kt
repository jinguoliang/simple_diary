package com.empty.jinux.simplediary.ui.diarydetail.fragment.edittools

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.ui.diarydetail.fragment.DiaryDetailFragment
import com.empty.jinux.simplediary.ui.diarydetail.fragment.MFragment
import kotlinx.android.synthetic.main.fragment_select_picture.*

class PictureSelectFragment : MFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_select_picture, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        selectPicture.setOnClickListener {
            mParentFragment.startActivityForResult(Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                it.addCategory(Intent.CATEGORY_DEFAULT)
            }, DiaryDetailFragment.REQUEST_SELECT_PICTURE)
        }
    }
}