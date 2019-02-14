package com.empty.jinux.simplediary.data.source.metercounter

interface MeterCounterDataSource {
    fun getAll(): List<MeterCounter>
    fun addOne(meterCounter: MeterCounter)
    fun updateOne(meterCounter: MeterCounter)
    fun deleteOne(meterCounter: MeterCounter)
}