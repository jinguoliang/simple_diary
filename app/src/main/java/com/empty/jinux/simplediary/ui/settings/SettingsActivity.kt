package com.empty.jinux.simplediary.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.databinding.ActivitySettingsBinding
import com.empty.jinux.simplediary.ui.LockHelper
import com.empty.jinux.simplediary.util.ActivityUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    @Inject
    lateinit var lockHelper: LockHelper

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }




}