package com.empty.jinux.simplediary.ui.main

import com.empty.jinux.simplediary.ui.diarydetail.fragment.StatisticsFragmentModule
import com.empty.jinux.simplediary.ui.main.diarylist.DiaryListFragmentModule
import com.empty.jinux.simplediary.ui.main.metercouner.MeterCounterFragmentModule
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [DiaryListFragmentModule::class,
    StatisticsFragmentModule::class,
    MeterCounterFragmentModule::class])
internal interface MainActivityComponent : AndroidInjector<MainActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MainActivity>()
}