package com.empty.jinux.simplediary.data.source.metercounter

data class MeterCounter(
        val id: Long = 0,
        val name: String,
        val unit: String,
        val records: List<Int>)

data class MeterCounterRecord(
        val time: Long,
        val value: Int
)