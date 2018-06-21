package com.empty.jinux.simplediary.data.backup

import android.os.Environment
import java.io.File

abstract class Backup {
    open fun login() = true

    abstract val needLogin: Boolean

    //ask to the user a name for the local and perform it. The local will be saved to a custom folder.
    fun performBackup(outFileName: String) {
        val folder = getBackupFolder()

        val success = if (folder.exists()) true else folder.mkdirs()

        if (success) {
            val out = "$folder/$outFileName.db"
            backupDb(out)
        }
    }

    //ask to the user what local to restore
    fun performImport(inFileName: String) {



    }

    fun tryLogin(): Boolean {
        if (needLogin) {
            return login()
        }
        return true
    }

    abstract fun importDb(path: String)

    abstract fun backupDb(out: String)

    fun getBackupFolder(): File {
        return File(Environment.getExternalStorageDirectory().toString() + File.separator + BACKUP_FOLDER)
    }

    companion object {
        const val BACKUP_FOLDER = "JDIARY"

    }
}