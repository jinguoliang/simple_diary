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


class LocalBackup(fragment: Fragment) : Backup() {
    val context = fragment.context!!

    override val needLogin: Boolean = false

    override fun backupDb(outPath: String) {
        val databaseFilePath = context.getDatabasePath(DATABASE_NAME).toString();
        Files.copy(File(databaseFilePath), File(outPath))
    }

    override fun importDb(inPath: String) {
        val databaseFilePath = context.getDatabasePath(DATABASE_NAME).toString();

        Files.copy(File(inPath), File(databaseFilePath))

        val databaseShm = "$databaseFilePath-shm"
        val databaseWal = "$databaseFilePath-wal"
        File(databaseShm).delete()
        File(databaseWal).delete()

    }

}