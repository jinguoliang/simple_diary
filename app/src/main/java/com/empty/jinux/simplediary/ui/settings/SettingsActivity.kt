package com.empty.jinux.simplediary.ui.settings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.util.ActivityUtils
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setupToolbar()

        ActivityUtils.addFragmentToActivity(supportFragmentManager, SettingsFragment(), R.id.content)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        val ab = supportActionBar
        ab!!.setDisplayHomeAsUpEnabled(true)
        ab.setDisplayShowHomeEnabled(true)
    }


}