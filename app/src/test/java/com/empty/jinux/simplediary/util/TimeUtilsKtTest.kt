package com.empty.jinux.simplediary.util

import org.junit.Test

import org.junit.Assert.*
import java.text.SimpleDateFormat

class
TimeUtilsKtTest {

    @Test
    fun weekStartTime_FirstDay() {
        val formater = SimpleDateFormat("yyyy/MM/dd HH:mm")
        val day = formater.parse("2018/04/01 22:22").time
        val expected = "2018/04/01 00:00"

        assertEquals(expected, formater.format(day.weekStartTime()))
    }

    @Test
    fun weekStartTime_LastDay() {
        val formater = SimpleDateFormat("yyyy/MM/dd HH:mm")
        val day = formater.parse("2018/04/07 22:22").time
        val expected = "2018/04/01 00:00"

        assertEquals(expected, formater.format(day.weekStartTime()))
    }

    @Test
    fun dayTime_start() {
        val formater = SimpleDateFormat("yyyy/MM/dd HH:mm")
        val day = formater.parse("2018/04/07 00:00").time
        val expected = "2018/04/07 00:00"

        assertEquals(expected, formater.format(day.dayTime()))
    }

    @Test
    fun dayTime_end() {
        val formater = SimpleDateFormat("yyyy/MM/dd HH:mm")
        val day = formater.parse("2018/04/07 23:59").time
        val expected = "2018/04/07 00:00"

        assertEquals(expected, formater.format(day.dayTime()))
    }
}