package com.empty.jinux.simplediary.util

import com.empty.jinux.baselibaray.utils.getFirstLine
import org.junit.Test

import org.junit.Assert.*

class TextUtilsKtTest {

    @Test
    fun getFirstLine_Short() {
        val text = "hello world\n" +
                "hahah this is good"
        val expected = "hello world"
        assertEquals(expected, text.getFirstLine())
    }

    @Test
    fun getFirstLine_Long() {
        val text = "hello world hahahahah this is a good\n" +
                "hahah this is good"
        val expected = "hello world hahahaha"
        assertEquals(expected, text.getFirstLine())
    }

    @Test
    fun getFirstLine_OneLine() {
        val text = "hello world hahahahah this is a good" +
                "hahah this is good"
        val expected = "hello world hahahaha"
        assertEquals(expected, text.getFirstLine())
    }

    @Test
    fun getFirstLine_Empty() {
        val text = ""
        val expected = ""
        assertEquals(expected, text.getFirstLine())
    }
}