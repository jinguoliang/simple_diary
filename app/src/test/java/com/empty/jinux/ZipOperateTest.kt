package com.empty.jinux

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.util.zip.*

class ZipOperateTest {
    val zipFileName = "/tmp/test.zip"

    @Test
    fun `create a zip`(): Unit {

        File(zipFileName).delete()

        val zipOut = ZipOutputStream(FileOutputStream(zipFileName))
        zipOut.setComment("hello world")
        zipOut.setLevel(9)
        zipOut.setMethod(ZipOutputStream.DEFLATED)

        val oneEntry = ZipEntry("one")
        oneEntry.extra = "ts".toByteArray()
        oneEntry.comment = "cccc"
        oneEntry.method = ZipEntry.STORED
        val createContent = createContent().toByteArray()
        oneEntry.size = createContent.size.toLong()
        oneEntry.compressedSize = createContent.size.toLong()
        val crC32 = CRC32()
        crC32.update(ByteBuffer.wrap(createContent))
        oneEntry.crc = crC32.value
        zipOut.putNextEntry(oneEntry)
        zipOut.write(createContent)
        zipOut.closeEntry()

        zipOut.close()

        assertTrue(File(zipFileName).isFile)
    }

    @Test
    fun `get info for a zip`(): Unit {
        val zipFile = ZipFile(zipFileName)
        assertEquals(zipFileName, zipFile.name)
        assertEquals("hello world", zipFile.comment)
        val entries = zipFile.entries().toList()
        assertEquals(1, entries.size)

        val entry = entries[0]
        assertEquals("one", entry.name)
        assertEquals("cccc", entry.comment)
    }

    @Test
    fun `read data from zip`(): Unit {
        val zipFile = ZipFile(zipFileName)
        val entries = zipFile.entries().toList()
        val one = entries[0]
        val inputStream = zipFile.getInputStream(one)
        val readText = inputStream.buffered().reader().readText()
        assertEquals("hello world", readText)
    }

    @Test
    fun `read data from zip stream`(): Unit {
        val zipStream = ZipInputStream(FileInputStream(zipFileName))
        val entry = zipStream.nextEntry
        assertEquals("one", entry.name)
        assertEquals("hello world", zipStream.bufferedReader().readText())
    }

    private fun createContent(): String {
        return (1..1).map { "hello world" }.joinToString { it }
    }
}