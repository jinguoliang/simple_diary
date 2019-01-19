package com.empty.jinux.simplediary.ui.main.metercouner

class MeterCounterPresenter(val view: MeterCounterContract.View) : MeterCounterContract.Presenter {
    override fun addNewRecord(title: String, counter: Int, meterCounterUnit: String) {

    }

    override fun loadData() {
        view.showRecords()
    }

    override fun start() {
        loadData()
    }



}
