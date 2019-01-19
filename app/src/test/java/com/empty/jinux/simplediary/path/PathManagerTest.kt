package com.empty.jinux.simplediary.path

import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.io.File

class PathManagerTest {
    @Test
    fun testCreate() {
        val mock = mock(PathManager::class.java)

        `when`(mock.getAppRoot()).thenReturn(File("/test"))
        assertPathName("/test/jin", mock.getAppInternalDir("jin"))

        `when`(mock.getExternalRoot()).thenReturn(File("/dang"))
        assertPathName("/dang/jin", mock.getExternalDir("jin"))
    }

    private fun assertPathName(pathName: String, file: File) {
        assertEquals(pathName, file.absolutePath)
    }
}