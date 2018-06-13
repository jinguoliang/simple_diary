package com.empty.jinux.baselibaray.view.about

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.empty.jinux.baselibaray.R

open abstract class AbsAboutActivity : AppCompatActivity()  {

    protected abstract fun onCreateHeader(icon: ImageView, slogan: TextView, version: TextView)
    protected abstract fun onItemsCreated(items: MutableList<Any>)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_abs_about)
    }


}