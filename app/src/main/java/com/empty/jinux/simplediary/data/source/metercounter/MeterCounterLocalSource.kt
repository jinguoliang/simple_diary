package com.empty.jinux.simplediary.data.source.metercounter

import android.content.Context
import androidx.room.Room
import com.empty.jinux.baselibaray.log.loge
import com.empty.jinux.simplediary.data.source.diary.local.room.DATABASE_NAME
import com.empty.jinux.simplediary.data.source.diary.local.room.DiaryDatabase
import com.empty.jinux.simplediary.data.source.metercounter.room.entity.MeterCounter as RoomMC
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
                MeterCounter(id!!, name, unit, records.split(",").map {
                    it.toIntOrNull() ?: 0
                })
            }
        }
    }

    override fun addOne(meterCounter: MeterCounter) {
        meterCounter.apply {
           val result =  table.insertOne(RoomMC(null, name, unit, records.joinToString(separator = ",")))
            loge(result)
        }
    }

    override fun updateOne(meterCounter: MeterCounter) {
        meterCounter.apply {
            table.updateState(RoomMC(id, name, unit, records.joinToString(separator = ",")))
        }
    }

    override fun deleteOne(meterCounter: MeterCounter) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}