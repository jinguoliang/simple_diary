package com.empty.jinux.simplediary.data.backup

import android.os.Environment
import com.empty.jinux.simplediary.path.PathManager
import java.io.File
import java.io.FileNotFoundException

abstract class Backup(open val pathManager: PathManager) {
    open fun login() = true

    abstract val needLogin: Boolean

    //ask to the user a name for the local and perform it. The local will be saved to a custom folder.
    fun performBackup(outFileName: String) {
        val folder = getBackupFolder()
        val success = pathManager.ensureFoldExist(folder)
        if (success) {
            val out = "$folder/$outFileName.db"
            backupDb(File(out))
        } else {
            throw FileNotFoundException("the backup folder does not exist: $folder")
        }
    }

    //ask the user what local to restore
    fun performImport(file: File) {
        importDb(file)
    }

    fun tryLogin(): Boolean {
        if (needLogin) {
            return login()
        }
        return true
    }

    abstract fun importDb(zip: File)

    abstract fun backupDb(to: File)

    // open for mock in test
    open fun getBackupFolder(): File {
        return File("""${Environment.getExternalStorageDirectory()}${File.separator}$BACKUP_FOLDER""")
    }

    fun getBackupList(): List<String> {
        return getBackupFiles().map { it.name }
    }

    // open for test
    open fun getBackupFiles(): List<File> {
        val folder = getBackupFolder()
        return if (folder.exists()) {
            folder.listFiles()?.run {
                sortedBy { it.lastModified() }.reversed()
            }?.toList() ?: listOf()
        } else listOf()
    }

    companion object {
        const val BACKUP_FOLDER = "JDIARY"

    }
}