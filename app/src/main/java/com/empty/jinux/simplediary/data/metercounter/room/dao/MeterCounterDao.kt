package com.empty.jinux.simplediary.data.metercounter.room.dao

import androidx.room.*
import com.empty.jinux.simplediary.data.metercounter.room.entity.MeterCounter
import com.empty.jinux.simplediary.data.source.local.room.COLUMN_ID
import com.empty.jinux.simplediary.data.source.local.room.TABLE_METER_COUNTER
import com.empty.jinux.simplediary.data.source.local.room.entity.Diary

@Dao
interface MeterCounterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(diary: MeterCounter): Long

    @Query("SELECT * FROM $TABLE_METER_COUNTER")
    fun getAll(): List<MeterCounter>

    @Query("SELECT * FROM $TABLE_METER_COUNTER WHERE \"id\" is :id LIMIT 1")
    fun getOneById(id: Long): MeterCounter?

    @Delete
    fun delete(meterCounter: MeterCounter)

    @Query("DELETE FROM $TABLE_METER_COUNTER WHERE \"id\" is :id")
    fun deleteById(id: Long)

    @Update()
    fun updateState(meterCounter: MeterCounter): Int
}