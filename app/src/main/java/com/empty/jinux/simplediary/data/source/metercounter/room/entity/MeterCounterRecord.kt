package com.empty.jinux.simplediary.data.source.metercounter.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.empty.jinux.simplediary.data.source.diary.local.room.COLUMN_ID
import com.empty.jinux.simplediary.data.source.diary.local.room.TABLE_METER_COUNTER

@Entity(tableName = TABLE_METER_COUNTER)
data class MeterCounter(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = COLUMN_ID) var id: Long?,
        @ColumnInfo(name = "name") var name: String,
        @ColumnInfo(name = "unit") var unit: String,
        @ColumnInfo(name = "records") var records: String
)

@Entity(foreignKeys = [ForeignKey(entity = MeterCounter::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("counterId"))])
data class MeterCounterRecord(
        @PrimaryKey(autoGenerate = true) var id: Long?,
        @ColumnInfo(name = "date") var date: Long?,
        @ColumnInfo(name = "value") var value: Int?,
        @ColumnInfo(name = "counterId") var counterId: Int?
)