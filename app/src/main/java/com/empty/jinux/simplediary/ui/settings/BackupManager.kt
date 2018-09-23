package com.empty.jinux.simplediary.ui.settings

import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import com.empty.jinux.baselibaray.view.loading.doTaskWithLoadingDialog
import com.empty.jinux.simplediary.BuildConfig
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.backup.Backup
import com.empty.jinux.simplediary.data.source.DiariesDataSource
import com.empty.jinux.simplediary.di.Local
import com.empty.jinux.simplediary.di.Remote
import com.empty.jinux.baselibaray.utils.formatBackupDate
import org.jetbrains.anko.toast
import java.io.File
import javax.inject.Inject

class BackupManager
@Inject internal constructor(
        val fragment: Fragment,
        @param:Local val local: Backup,
        @param:Local val localDatabase: DiariesDataSource
) {
    val activity = fragment.activity!!

    fun performLocalBackup() {
        activity.doTaskWithLoadingDialog(activity.getString(R.string.saving)) {
            if (local.tryLogin()) {
                val outFileName = "${if (BuildConfig.DEBUG) "debug_" else ""}${System.currentTimeMillis().formatBackupDate()}"
                local.performBackup(outFileName)
                fragment.activity!!.toast(R.string.successfully_backup)
            }
        }
    }

    fun performLocalRestore() {
        if (local.tryLogin()) {
            val folder = local.getBackupFolder()
            if (folder.exists()) {
                folder.listFiles()?.apply {
                    sortBy { it.lastModified() }
                    reverse()
                    showSingleSelectDialog(this)
                }
            }
        }
    }

    private fun showSingleSelectDialog(files: Array<out File>) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.restore_from_local_dialog_title)
        builder.setNegativeButton(R.string.dialog_cancel) { dialog, which ->
            dialog.dismiss()
        }
        builder.setItems(files.map { it.name }.toTypedArray()) { _, which ->
            try {
                activity.doTaskWithLoadingDialog(fragment.getString(R.string.restore)) {
                    local.importDb(files[which].path)
                    localDatabase.refreshDiaries()
                }
            } catch (e: Exception) {
                activity.toast("Failed to restore")
            }
        }
        builder.show()
    }
}