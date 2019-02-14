package com.empty.jinux.simplediary.data.backup

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.empty.jinux.simplediary.data.source.diary.local.room.DATABASE_NAME
import com.empty.jinux.simplediary.path.PathManager
import com.google.common.io.Files
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream


class LocalBackup(val context: Context, override val pathManager: PathManager) : Backup(pathManager) {

    override val needLogin: Boolean = false

    override fun backupDb(to: File) {
        compressToZip(to, collectBackupData())
    }

    @VisibleForTesting
    fun collectBackupData(): List<File> {
        val list = arrayListOf<File>()

        val databaseFilePath = context.getDatabasePath(DATABASE_NAME)
        list.add(databaseFilePath)

        list.add(File(pathManager.getAppInternalDir("shared_prefs/${context.packageName}"), "_preferences"))

        val imagesDir = pathManager.getAppInternalDir("images")
        if (imagesDir.exists() && imagesDir.isDirectory) {
            for (x in imagesDir.listFiles()) {
                list.add(x)
            }
        }
        return list
    }

    private fun compressToZip(zip: File, list: List<File>) {
        val zipOut = ZipOutputStream(FileOutputStream(zip))
        for (e in list) {
            zipOut.putNextEntry(e.toZipEntry())
            Files.copy(e, zipOut)
            zipOut.closeEntry()
        }
        zipOut.close()
    }

    private fun File.toZipEntry() = ZipEntry(this.name)

    override fun importDb(zip: File) {
        pathManager.ensureFoldExist(pathManager.getAppInternalDir("images"))
        pathManager.ensureFoldExist(pathManager.getAppInternalDir("databases"))
        pathManager.ensureFoldExist(pathManager.getAppInternalDir("shared_prefs"))

        restoreFromZip(zip, pathManager.getAppRoot())

        val databaseFilePath = context.getDatabasePath(DATABASE_NAME).toString()
        val databaseShm = "$databaseFilePath-shm"
        val databaseWal = "$databaseFilePath-wal"
        File(databaseShm).delete()
        File(databaseWal).delete()
    }

    private fun restoreFromZip(zipFile: File, rootDir: File) {
        val zip = ZipFile(zipFile)
        for (entry in zip.entries().toList()) {
            val outputStream = FileOutputStream(File(rootDir, entry.name))
            val `in` = zip.getInputStream(entry)
            copy(outputStream, `in`)
            outputStream.close()
        }
        zip.close()
    }

    private fun copy(outputStream: OutputStream, zipIn: InputStream) {
        val buffer = ByteArray(64)
        var n = zipIn.read(buffer)
        while (n > 0) {
            outputStream.write(buffer, 0, n)
            n = zipIn.read(buffer)
        }
        outputStream.close()
    }

}
