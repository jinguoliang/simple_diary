package com.empty.jinux.simplediary.data.backup

import android.support.v4.app.Fragment
import com.empty.jinux.simplediary.data.source.local.room.DATABASE_NAME
import com.google.common.io.Files
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream


class LocalBackup(fragment: Fragment) : Backup() {
    val context = fragment.context!!

    override val needLogin: Boolean = false

    override fun backupDb(outPath: String) {
        val databaseFilePath = context.getDatabasePath(DATABASE_NAME).toString();
        val zipOut = ZipOutputStream(FileOutputStream(outPath))
        zipOut.putNextEntry(ZipEntry(databaseFilePath))
        Files.copy(File(databaseFilePath), zipOut)

        val imagesDir = "${context!!.filesDir}/images".run { File(this) }
        if (imagesDir.isDirectory) {
            for (x in imagesDir.listFiles()) {
                zipOut.putNextEntry(ZipEntry(x.absolutePath))
                Files.copy(x, zipOut)
                zipOut.closeEntry()

            }
        }
        zipOut.flush()
        zipOut.close()
    }

    override fun importDb(inPath: String) {
        val databaseFilePath = context.getDatabasePath(DATABASE_NAME).toString();

        val zipIn = ZipInputStream(FileInputStream(inPath))
        var entry: ZipEntry? = zipIn.nextEntry
        while (null != entry) {
            copy(zipIn, entry.name)
            zipIn.closeEntry()
            entry = zipIn.nextEntry
        }
        zipIn.close()

        val databaseShm = "$databaseFilePath-shm"
        val databaseWal = "$databaseFilePath-wal"
        File(databaseShm).delete()
        File(databaseWal).delete()
    }

    private fun copy(zipIn: ZipInputStream, name: String) {
        val buffer = ByteArray(64)
        var n = zipIn.read(buffer)
        val fileOutputStream = FileOutputStream(name)
        while (n > 0) {
            fileOutputStream.write(buffer, 0, n)
            n = zipIn.read(buffer)
        }
        fileOutputStream.close()
    }

}