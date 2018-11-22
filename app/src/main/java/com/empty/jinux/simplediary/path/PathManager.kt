package com.empty.jinux.simplediary.path

import android.content.Context
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PathManager @Inject constructor(val context: Context) {
    fun getAppInternalDir(child: String = ""): File {
        val root = context.getDir("databases", Context.MODE_PRIVATE).parentFile
        return if (child.isEmpty()) root
        else File(root, child).apply { ensureFoldExist(this) }
    }

    // open for mock in test
    open fun ensureFoldExist(folder: File) = if (folder.exists()) true else folder.mkdirs()
}