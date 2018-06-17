package com.empty.jinux.simplediary.data.backup

import android.content.Context
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.toast
import com.empty.jinux.baselibaray.log.loge
import com.empty.jinux.simplediary.R
import com.empty.jinux.simplediary.data.source.local.room.DATABASE_NAME
import com.google.common.io.Files
import java.io.File


class LocalBackup(private val fragment: Fragment) : Backup {
    val activity = fragment.activity!!

    //ask to the user a name for the local and perform it. The local will be saved to a custom folder.
    override fun performBackup(outFileName: String) {
        val folder = File(Environment.getExternalStorageDirectory().toString() + File.separator + activity.getString(R.string.app_name))

        var success = true
        if (!folder.exists())
            success = folder.mkdirs()

        if (success) {
            val out = "$folder/$outFileName.db"
            backupDb(activity, out)
            activity.toast("local successfully $out")
        } else
            Toast.makeText(activity, "Unable to create directory. Retry", Toast.LENGTH_SHORT).show()
    }


    //ask to the user what local to restore
    override fun performImport(inFileName: String) {
        val folder = File(Environment.getExternalStorageDirectory().toString() + File.separator + activity.getString(R.string.app_name))
        if (folder.exists()) {

            val files = folder.listFiles()

            val arrayAdapter = ArrayAdapter<String>(activity, android.R.layout.select_dialog_item)
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
                    loge("the path = ${files[which].path}")
                    importDb(activity, files[which].path)
                } catch (e: Exception) {
                    Toast.makeText(activity, "Unable to restore. Retry", Toast.LENGTH_SHORT).show()
                }
            }
            builderSingle.show()
        } else
            Toast.makeText(activity, "Backup folder not present.\nDo a local before a restore!", Toast.LENGTH_SHORT).show()
    }


    private fun backupDb(context: Context, outPath: String) {
        loge("out path = $outPath", "JIN")
        val databaseFilePath = context.getDatabasePath(DATABASE_NAME).toString();
        loge("in path = $databaseFilePath", "JIN")
        Files.copy(File(databaseFilePath), File(outPath))
    }

    private fun importDb(context: Context, inPath: String) {
        val databaseFilePath = context.getDatabasePath(DATABASE_NAME).toString();

        Files.copy(File(inPath), File(databaseFilePath))

        val databaseShm = "$databaseFilePath-shm"
        val databaseWal = "$databaseFilePath-wal"
        File(databaseShm).delete()
        File(databaseWal).delete()

    }

}