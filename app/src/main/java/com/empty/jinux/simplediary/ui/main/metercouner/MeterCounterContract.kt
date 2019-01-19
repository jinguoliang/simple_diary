package com.empty.jinux.simplediary.ui.main.metercouner

import com.empty.jinux.simplediary.BasePresenter
import com.empty.jinux.simplediary.BaseView

interface MeterCounterContract {
    interface View : BaseView<Presenter> {
        fun showRecords()
        fun addNewRecord()
    }

    interface Presenter : BasePresenter {
        fun loadData()
        fun addNewRecord(title: String, counter: Int, meterCounterUnit: String)
    }
}