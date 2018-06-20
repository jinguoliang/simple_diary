@file:Suppress("DEPRECATION")

package com.empty.jinux.baselibaray.view.loading

import android.app.ProgressDialog
import android.content.Context
import com.empty.jinux.baselibaray.R
import com.empty.jinux.baselibaray.thread.ThreadPools


fun Context.doTaskWithLoadingDialog(msg: String, task: () -> Unit) {
    val d = ProgressDialog.show(this, "", msg)
    ThreadPools.postOnUIDelayed(1000) {
        task.invoke()
        ThreadPools.postOnUI {
            d.dismiss()
        }
    }
}

class KLoadingDialog {

}