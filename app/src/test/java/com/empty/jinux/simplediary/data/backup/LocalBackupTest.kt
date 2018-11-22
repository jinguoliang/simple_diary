package com.empty.jinux.simplediary.data.backup

import android.content.Context
import org.junit.After
import org.junit.Assert
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
        backup = LocalBackup(mockitoContext)
    }

    @After
    fun teardown(): Unit {

    }

    @Test
    fun getNeedLogin() {
        assertEquals(false, backup.needLogin)
    }

    @Test
    fun backupDb() {
        Mockito.`when`(mockitoContext.getDatabasePath(Mockito.anyString())).thenReturn(File("hello"))
        backup.backupDb("hello")

    }

    @Test
    fun importDb() {
    }
}