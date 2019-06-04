package com.empty.jinux.simplediary.data.backup

import com.empty.jinux.simplediary.data.source.local.room.DATABASE_NAME
import com.google.common.io.Files
import java.io.File


class LocalBackup(fragment: androidx.fragment.app.Fragment) : Backup() {
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