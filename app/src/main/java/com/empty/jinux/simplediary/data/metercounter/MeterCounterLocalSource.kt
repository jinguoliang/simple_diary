package com.empty.jinux.simplediary.data.metercounter

import android.content.Context
import androidx.room.Room
import com.empty.jinux.baselibaray.log.loge
import com.empty.jinux.simplediary.data.source.local.room.DATABASE_NAME
import com.empty.jinux.simplediary.data.source.local.room.DiaryDatabase
import com.empty.jinux.simplediary.data.metercounter.room.entity.MeterCounter as RoomMC
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MeterCounterLocalSource
@Inject
constructor(val context: Context) : MeterCounterDataSource {

    private var table = Room.databaseBuilder(
            context.applicationContext,
            DiaryDatabase::class.java, DATABASE_NAME)
            .build()
            .meterCounterDao()

    override fun getAll(): List<MeterCounter> {
        return table.getAll().map {
            it.run {
                MeterCounter(name, unit, records.split(",").map { it.toIntOrNull() ?: 0 })
            }
        }
    }

    override fun addOne(meterCounter: MeterCounter) {
        meterCounter.apply {
           val result =  table.insertOne(RoomMC(name, unit, records.joinToString(separator = ",")))
            loge(result)
        }
    }

    override fun updateOne(meterCounter: MeterCounter) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteOne(meterCounter: MeterCounter) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}