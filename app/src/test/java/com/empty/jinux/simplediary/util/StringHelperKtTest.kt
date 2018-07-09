package com.empty.jinux.simplediary.util

import com.empty.jinux.baselibaray.utils.wordsCount
import org.junit.Test

import org.junit.Assert.*

class StringHelperKtTest {

    @Test
    fun wordsCount_english() {
        val text = "hello world this is a test"
        assertEquals(6, text.wordsCount())
    }

    @Test
    fun wordsCount_chinese() {
        val text = "这是个测试"
        assertEquals(5, text.wordsCount())
    }

    @Test
    fun wordsCount_chinese_english() {
        val text = "这 h w"
        assertEquals(3, text.wordsCount())
    }

    @Test
    fun wordsCount_chinese_english1() {
        val text = "h我 w"
        assertEquals(3, text.wordsCount())
    }

    @Test
    fun wordsCount_chinese_english_with_punctuation() {
        val text = "我，w，hello。"
        assertEquals(3, text.wordsCount())
    }


}