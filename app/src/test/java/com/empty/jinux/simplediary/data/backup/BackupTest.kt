package com.empty.jinux.simplediary.data.backup

import com.empty.jinux.simplediary.path.PathManager
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.runners.MockitoJUnitRunner
import java.io.File
import java.io.FileNotFoundException

@RunWith(MockitoJUnitRunner::class)
class BackupTest {

    lateinit var mockBackup: Backup

    lateinit var mockPathManager: PathManager

    @Before
    fun setUp() {
        mockBackup = mock(Backup::class.java)
        mockPathManager = mock(PathManager::class.java)
        `when`(mockBackup.pathManager).thenReturn(mockPathManager)
    }

    @Test
    fun `when backup folder is exist, call performBackup`() {
        val parentPath = File("parent")
        val outFileName = "hello"
        val expectation = "$parentPath/hello.db"

        `when`(mockBackup.getBackupFolder()).thenReturn(parentPath)
        `when`(mockPathManager.ensureFoldExist(parentPath)).thenReturn(true)

        mockBackup.performBackup(outFileName)

        verify(mockBackup).backupDb(expectation)
    }

    @Test
    fun `when backup fold not is exist, trow exception`() {
        val parentPath = File("parent")
        val outFileName = "hello"

        `when`(mockBackup.getBackupFolder()).thenReturn(parentPath)
//        `when`(mockBackup.ensureFoldExist(parentPath)).thenReturn(false)

        try {
            mockBackup.performBackup(outFileName)
        } catch (e: FileNotFoundException) {
            assertEquals("the backup folder does not exist: $parentPath", e.message)
            return
        }
        fail("No Exception")
    }

    @Test
    fun performImport() {
        mockBackup.performImport("hello")
        verify(mockBackup).importDb("hello")
    }

    @Test
    fun `try login when not need`(): Unit {
        `when`(mockBackup.needLogin).thenReturn(false)
        val result = mockBackup.tryLogin()
        assertEquals(true, result)
    }

    @Test
    fun `try login when need`(): Unit {
        `when`(mockBackup.needLogin).thenReturn(true)

        var expected = true
        `when`(mockBackup.login()).thenReturn(expected)
        var result = mockBackup.tryLogin()
        assertEquals(expected, result)

        expected = false
        `when`(mockBackup.login()).thenReturn(expected)
        result = mockBackup.tryLogin()
        assertEquals(expected, result)
    }

    @Test
    fun `getBackupList`(): Unit {
        val expected = arrayListOf("a.bak", "b.bak")
        `when`(mockBackup.getBackupFiles()).thenReturn(expected.map { File(it) })
        val backups = mockBackup.getBackupList()
        assertEquals(expected, backups)
    }
}

