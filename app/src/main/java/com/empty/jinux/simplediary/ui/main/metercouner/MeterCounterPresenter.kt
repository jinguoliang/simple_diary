package com.empty.jinux.simplediary.ui.main.metercouner

import com.empty.jinux.simplediary.data.metercounter.MeterCounter
import com.empty.jinux.simplediary.data.metercounter.MeterCounterDataSource
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import javax.inject.Inject

class MeterCounterPresenter
@Inject
constructor(private  var meterCounterSource: MeterCounterDataSource,
            val view: MeterCounterContract.View) : MeterCounterContract.Presenter {


    override fun addNewRecord(title: String, counter: Int, meterCounterUnit: String) {
        doAsync {
            meterCounterSource.addOne(MeterCounter(title, meterCounterUnit, listOf(counter)))
            val data = meterCounterSource.getAll()
            uiThread {
                view.showRecords(data)
            }
        }
    }

    override fun loadData() {
        doAsync {
            val data = meterCounterSource.getAll()
            uiThread {
                view.showRecords(data)
            }
        }
    }

    override fun start() {
        loadData()
    }



}
