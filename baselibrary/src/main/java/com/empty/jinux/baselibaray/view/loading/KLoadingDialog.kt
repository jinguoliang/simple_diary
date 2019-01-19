@file:Suppress("DEPRECATION")

package com.empty.jinux.baselibaray.view.loading

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import com.empty.jinux.baselibaray.R
import com.empty.jinux.baselibaray.thread.ThreadPools


fun Activity.doTaskWithLoadingDialog(msg: String, delay: Long = 1000, task: () -> Unit) {
    val d = ProgressDialog.show(this, "", msg)
    ThreadPools.postOnQuene {
        task.invoke()
        ThreadPools.postOnUIDelayed(delay) {
            d.dismiss()
        }
    }
}

class KLoadingDialog {

}