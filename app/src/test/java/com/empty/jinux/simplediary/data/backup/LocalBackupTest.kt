package com.empty.jinux.simplediary.data.backup

import android.content.Context
import com.empty.jinux.simplediary.path.PathManager
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import java.io.File

class LocalBackupTest {

    lateinit var backup: LocalBackup
    lateinit var mockitoContext: Context
    @Before
    fun setup(): Unit {
        mockitoContext = mock(Context::class.java)
        backup = LocalBackup(mockitoContext, mock(PathManager::class.java))
    }

    @After
    fun teardown(): Unit {

    }

    @Test
    fun getNeedLogin() {
        assertEquals(false, backup.needLogin)
    }

    @Test
    fun `test collect backup files`() {
        val pkgName = "pppppp"
        val expected = arrayListOf(File("hello/haha"), File("shared_prefs/$pkgName/_preferences")).toTypedArray()
        Mockito.`when`(mockitoContext.getDatabasePath(Mockito.anyString())).thenReturn(expected[0])
        Mockito.`when`(mockitoContext.packageName).thenReturn(pkgName)

        val collected = backup.collectBackupData()
        assertArrayEquals(expected, collected.toTypedArray())
    }

    @Test
    fun importDb() {
    }
}