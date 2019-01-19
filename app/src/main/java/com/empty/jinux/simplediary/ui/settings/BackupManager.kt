package com.empty.jinux.simplediary.ui.settings

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.empty.jinux.baselibaray.log.logThrowable
import com.empty.jinux.baselibaray.utils.formatBackupDate
import com.empty.jinux.baselibaray.view.loading.doTaskWithLoadingDialog
import com.empty.jinux.simplediary.BuildConfig
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.backup.Backup
import com.empty.jinux.simplediary.data.source.DiariesDataSource
import com.empty.jinux.simplediary.di.Local
import com.empty.jinux.simplediary.di.Remote
import org.jetbrains.anko.toast
import java.io.File
import javax.inject.Inject

class BackupManager
@Inject internal constructor(
        val context: Activity,
        @param:Local val local: Backup,
        @param:Local val localDatabase: DiariesDataSource
) {

    fun performLocalBackup() {
        context.doTaskWithLoadingDialog(context.getString(R.string.saving)) {
            tryBackup()
        }
    }

    private fun tryBackup() {
        if (local.tryLogin()) {
            val outFileName = generateBackupFileName()
            local.performBackup(outFileName)
            context.toast(R.string.successfully_backup)
        } else {
            throw LoginFailedException()
        }
    }

    private fun generateBackupFileName() =
            "${if (BuildConfig.DEBUG) "debug_" else ""}${System.currentTimeMillis().formatBackupDate()}"

    fun performLocalRestore() {
        if (local.tryLogin()) {
            local.getBackupFiles().let { backups ->
                showSingleSelectDialog(backups.toTypedArray()) {
                    try {
                        local.importDb(File(it))
                        localDatabase.refreshDiaries()
                    } catch (e: Exception) {
                        context.toast("Failed to restore")
                        logThrowable(e)
                    }
                }
            }
        }
    }

    private fun showSingleSelectDialog(files: Array<out File>, onSelected: (path: String) -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.restore_from_local_dialog_title)
        builder.setNegativeButton(R.string.dialog_cancel) { dialog, _ ->
            dialog.dismiss()
        }
        builder.setItems(files.map { it.name }.toTypedArray()) { _, which ->
            context.doTaskWithLoadingDialog(context.getString(R.string.restore)) {
                onSelected(files[which].path)
            }
        }
        builder.show()
    }
}