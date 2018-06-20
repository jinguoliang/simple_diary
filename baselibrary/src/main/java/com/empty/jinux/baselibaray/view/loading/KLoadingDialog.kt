@file:Suppress("DEPRECATION")

package com.empty.jinux.baselibaray.view.loading

import android.app.ProgressDialog
import android.content.Context
import android.widget.ProgressBar
import com.empty.jinux.baselibaray.thread.ThreadPools


fun doTaskWithLoadingDialog(context: Context, task: () -> Unit) {
    val d = ProgressDialog.show(context, "", "Loading...")
    ThreadPools.postOnUIDelayed(1000) {
        task.invoke()
        ThreadPools.postOnUI {
            d.dismiss()
        }
    }
}

class KLoadingDialog {

}