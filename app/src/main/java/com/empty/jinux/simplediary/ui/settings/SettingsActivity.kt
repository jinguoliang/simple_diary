package com.empty.jinux.simplediary.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.ui.LockHelper
import com.empty.jinux.simplediary.util.ActivityUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_settings.*
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }




}