package com.empty.jinux.simplediary.ui.settings

import android.R
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.widget.ArrayAdapter
import com.empty.jinux.simplediary.data.backup.Backup
import com.empty.jinux.simplediary.di.Local
import com.empty.jinux.simplediary.di.Remote
import org.jetbrains.anko.toast
import java.io.File
import javax.inject.Inject

class BackupManager
@Inject internal constructor(val fragment: Fragment, @param:Local val local: Backup,
                             @param:Remote val remote: Backup) {
    val activity = fragment.activity!!

    fun performLocalBackup() {
        if (local.tryLogin()) {
            val outFileName = "jdiary_backup_${System.currentTimeMillis()}"
            local.performBackup(outFileName)
            fragment.activity!!.toast("Backup Successfully")
        }
    }

    fun performLocalRestore() {
        if (local.tryLogin()) {
            val folder = local.getBackupFolder()
            if (folder.exists()) {
                showSingleSelectDialog(folder.listFiles()!!)
            }
        }
    }

    private fun showSingleSelectDialog(files: Array<out File>) {
        val arrayAdapter = ArrayAdapter<String>(activity, R.layout.select_dialog_item)
        for (file in files)
            arrayAdapter.add(file.name)

        val builderSingle = AlertDialog.Builder(activity)
        builderSingle.setTitle("Restore:")
        builderSingle.setNegativeButton(
                "cancel"
        ) { dialog, which -> dialog.dismiss() }
        builderSingle.setAdapter(
                arrayAdapter
        ) { dialog, which ->
            try {
                local.importDb(files[which].path)
            } catch (e: Exception) {
            }
        }
        builderSingle.show()
    }
}