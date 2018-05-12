package com.empty.jinux.simplediary.report

import android.content.Context
import android.os.Bundle
import com.empty.jinux.simplediary.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics

class FirebaseReporter(context: Context) : Reporter {
    private val firebase = FirebaseAnalytics.getInstance(context).apply {
        setUserProperty("debug", "" + BuildConfig.DEBUG)
    }

    override fun reportEvent(e: String, args: Bundle) {
        firebase.logEvent(e, args)
    }

}