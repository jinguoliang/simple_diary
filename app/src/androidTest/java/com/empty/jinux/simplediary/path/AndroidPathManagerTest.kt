package com.empty.jinux.simplediary.path

import android.content.Context
import android.os.Environment
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class AndroidPathManagerTest {

    private lateinit var context: Context

    @Before
    fun setup(): Unit {
        context = InstrumentationRegistry.getTargetContext()
        File("/data/user/0/com.empty.jinux.simplediary.debug/files/$TEST_FILE_NAME").delete()
    }

    @Test
    fun testAndroidDirectories(): Unit {
        assertEquals("/storage/emulated/0", Environment.getExternalStorageDirectory().absolutePath)

        assertNotNull(context)
        assertEquals("/data/user/0/com.empty.jinux.simplediary.debug/app_haha", context.getDir("haha", Context.MODE_PRIVATE)?.absolutePath)

        assertEquals("/storage/emulated/0/Android/data/com.empty.jinux.simplediary.debug/files/Music", context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.absolutePath)
    }

    @Test
    fun testExternalFileDirs(): Unit {
        val externalFilesDirs = context.getExternalFilesDirs(Environment.DIRECTORY_DCIM)
        assertEquals(1, externalFilesDirs.size)
        assertEquals("/storage/emulated/0/Android/data/com.empty.jinux.simplediary.debug/files/DCIM",
                externalFilesDirs[0].absolutePath)
    }

    private val TEST_FILE_NAME = "hello"

    @Test
    fun testPrivateFileOperate(): Unit {

        assertEquals("/data/user/0/com.empty.jinux.simplediary.debug/files", context.filesDir.absolutePath)

        val out = context.openFileOutput(TEST_FILE_NAME, Context.MODE_PRIVATE)
        out.bufferedWriter().apply {
            write("hello world")
            close()
        }

        val fileList = context.fileList()
        assertNotEquals(0, fileList.size)

        assertEquals("/data/user/0/com.empty.jinux.simplediary.debug/files/$TEST_FILE_NAME", context.getFileStreamPath(TEST_FILE_NAME)?.absolutePath)


        val input = context.openFileInput(TEST_FILE_NAME)
        input.bufferedReader().apply {
            val s = readLine()
            assertEquals("hello world", s)
            close()
        }

        context.deleteFile(TEST_FILE_NAME)
        assertEquals(fileList.size - 1, context.fileList().size)


        assertEquals("/data/user/0/com.empty.jinux.simplediary.debug/no_backup", context.noBackupFilesDir.absolutePath)

    }
}