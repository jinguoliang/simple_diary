package com.empty.jinux.simplediary.ui.main

import com.empty.jinux.simplediary.report.ReportModule
import com.empty.jinux.simplediary.ui.diarydetail.fragment.StatisticsFragmentModule
import com.empty.jinux.simplediary.ui.main.diarylist.DiaryListFragmentModule
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = arrayOf(
        DiaryListFragmentModule::class,
        StatisticsFragmentModule::class,
        ReportModule::class))
internal interface MainActivityComponent : AndroidInjector<MainActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MainActivity>()
}