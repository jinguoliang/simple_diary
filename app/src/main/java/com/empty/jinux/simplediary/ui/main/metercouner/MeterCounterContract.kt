package com.empty.jinux.simplediary.ui.main.metercouner

import com.empty.jinux.simplediary.BasePresenter
import com.empty.jinux.simplediary.BaseView
import com.empty.jinux.simplediary.data.metercounter.MeterCounter

interface MeterCounterContract {
    interface View : BaseView<Presenter> {
        fun showRecords(data: List<MeterCounter>)
        fun addNewRecord()
    }

    interface Presenter : BasePresenter {
        fun loadData()
        fun addNewRecord(title: String, counter: Int, meterCounterUnit: String)
    }
}