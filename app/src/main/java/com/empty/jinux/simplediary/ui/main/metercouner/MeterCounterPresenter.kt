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


    override fun addNewRecord(meterCounter: MeterCounter) {
        doAsync {
            meterCounterSource.addOne(meterCounter)
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

    override fun updateRecord(meterCounter: MeterCounter) {
        doAsync {
            meterCounterSource.updateOne(meterCounter)
            loadData()
        }
    }

    override fun start() {
        loadData()
    }



}
