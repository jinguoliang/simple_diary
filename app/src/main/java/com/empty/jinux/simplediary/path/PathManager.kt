package com.empty.jinux.simplediary.path

import java.io.File
import javax.inject.Singleton

/**
 * We can get all kind path from here
 * app root is the app internal save root, it may be remove when the app is removed
 * external root is a directory, where we can save files that will survive after the app is removed
 */
@Singleton
abstract class PathManager {
    abstract fun getAppRoot(): File

    abstract fun getExternalRoot(): File

    // open for mock in test
    open fun ensureFoldExist(folder: File) = if (folder.exists()) true else folder.mkdirs()

    fun getAppInternalDir(child: String): File {
        return File(getAppRoot(), child)
    }

    fun getExternalDir(child: String): File {
        return File(getExternalRoot(), child)
    }
}