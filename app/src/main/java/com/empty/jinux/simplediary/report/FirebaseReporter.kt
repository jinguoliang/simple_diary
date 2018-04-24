package com.empty.jinux.simplediary.report

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class FirebaseReporter(context: Context) : Reporter {
    private val firebase = FirebaseAnalytics.getInstance(context)

    override fun reportEvent(e: String, args: Bundle) {
        firebase.logEvent(e, args)
    }

}