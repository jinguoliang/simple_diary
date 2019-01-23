package com.empty.jinux.simplediary.data.metercounter

data class MeterCounter(
        val name: String,
        val unit: String,
        val records: List<Int>)

data class MeterCounterRecord(
        val time: Long,
        val value: Int
)