package com.empty.jinux.simplediary.ui.settings

import android.os.Bundle
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.ui.LockHelper
import com.empty.jinux.simplediary.util.ActivityUtils
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*
import javax.inject.Inject


class SettingsActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var lockHelper: LockHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setupToolbar()

        ActivityUtils.addFragmentToActivity(supportFragmentManager, SettingsFragment(), R.id.content)
    }

    override fun onStart() {
        super.onStart()
        lockHelper.onStart(this)
    }

    override fun onStop() {
        super.onStop()
        lockHelper.onStop()
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