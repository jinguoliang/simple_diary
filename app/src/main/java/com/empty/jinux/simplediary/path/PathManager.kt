package com.empty.jinux.simplediary.path

import java.io.File
import javax.inject.Singleton

@Singleton
interface PathManager {
    fun getAppRoot(): File

    // open for mock in test
    open fun ensureFoldExist(folder: File) = if (folder.exists()) true else folder.mkdirs()

    fun getAppInternalDir(child: String): File {
        return File(getAppRoot(), child)
    }
}