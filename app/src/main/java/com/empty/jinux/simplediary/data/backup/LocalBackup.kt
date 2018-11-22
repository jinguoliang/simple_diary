package com.empty.jinux.simplediary.data.backup

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.empty.jinux.baselibaray.log.loge
import com.empty.jinux.simplediary.data.source.local.room.DATABASE_NAME
import com.empty.jinux.simplediary.path.PathManager
import com.google.android.gms.common.util.SharedPreferencesUtils
import com.google.common.io.Files
import org.jetbrains.anko.defaultSharedPreferences
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream


class LocalBackup(val context: Context, override val pathManager: PathManager) : Backup(pathManager) {

    override val needLogin: Boolean = false

    override fun backupDb(out: String) {

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

        compressToZip(out, list)
    }

    private fun compressToZip(out: String, map: List<File>) {
        val zipOut = ZipOutputStream(FileOutputStream(out))

        for (e in map) {
            zipOut.putNextEntry(ZipEntry(getZipEntryKey(e.absolutePath).apply { loge("the path = $this") }))
            Files.copy(e, zipOut)
            zipOut.closeEntry()
        }

        zipOut.flush()
        zipOut.close()
    }

    override fun importDb(path: String) {
        pathManager.ensureFoldExist(pathManager.getAppInternalDir("images"))
        pathManager.ensureFoldExist(pathManager.getAppInternalDir("databases"))
        pathManager.ensureFoldExist(pathManager.getAppInternalDir("shared_prefs"))

        val zipIn = ZipInputStream(FileInputStream(path))
        var entry: ZipEntry? = zipIn.nextEntry
        while (null != entry) {
            copy(zipIn, FileOutputStream(pathManager.getAppInternalDir(entry.name)))
            zipIn.closeEntry()
            entry = zipIn.nextEntry
        }
        zipIn.close()

        val databaseFilePath = context.getDatabasePath(DATABASE_NAME).toString()
        val databaseShm = "$databaseFilePath-shm"
        val databaseWal = "$databaseFilePath-wal"
        File(databaseShm).delete()
        File(databaseWal).delete()
    }

    private fun getZipEntryKey(databaseFilePath: String) = databaseFilePath.substring(pathManager.getAppInternalDir("a").parentFile.absolutePath.length)

    private fun copy(zipIn: InputStream, outputStream: OutputStream) {
        val buffer = ByteArray(64)
        var n = zipIn.read(buffer)
        while (n > 0) {
            outputStream.write(buffer, 0, n)
            n = zipIn.read(buffer)
        }
        outputStream.close()
    }

}
