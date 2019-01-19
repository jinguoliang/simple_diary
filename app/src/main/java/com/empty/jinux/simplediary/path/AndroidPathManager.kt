package com.empty.jinux.simplediary.path

import android.content.Context
import android.os.Environment
import java.io.File
import javax.inject.Inject

class AndroidPathManager @Inject constructor(val context: Context) : PathManager() {
    override fun getExternalRoot(): File {
        return Environment.getExternalStorageDirectory()
    }

    override fun getAppRoot(): File {
        return context.getDir("databases", Context.MODE_PRIVATE).parentFile
    }
}