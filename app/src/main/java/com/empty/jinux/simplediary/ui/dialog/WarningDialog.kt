package com.empty.jinux.simplediary.ui.dialog

import android.app.AlertDialog
import android.content.Context
import com.empty.jinux.simplediary.R

/**
 * Created by jinux on 18-4-10.
 */
fun Context.showWarningDialog(msg: String, next: () -> Unit) {
    AlertDialog.Builder(this).apply {
        setMessage(msg)
        setIcon(R.drawable.ic_warning_black_24dp)
        setPositiveButton(R.string.ok) { _, _ -> next()}
        setNegativeButton(R.string.cancel) { _, _ -> }
    }.create().show()
}