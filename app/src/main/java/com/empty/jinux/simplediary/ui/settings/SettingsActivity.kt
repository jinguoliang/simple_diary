package com.empty.jinux.simplediary.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toolbar
import com.empty.jinux.simplediary.R
import android.widget.LinearLayout



class SettingsActivity : DaggerPreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        val root = findViewById<View>(android.R.id.list).getParent().getParent().getParent() as LinearLayout
        val bar = LayoutInflater.from(this).inflate(R.layout.layout_actionbar, root, false) as Toolbar
        root.addView(bar, 0) // insert at top
        bar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish()
            }

        })
    }

}