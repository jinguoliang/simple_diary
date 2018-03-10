package com.empty.jinux.simplediary.ui.main.statistics

import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = arrayOf(
        StatisticsPresenterModule::class
))
internal interface StatisticsFragmentComponent : AndroidInjector<StatisticsFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<StatisticsFragment>()
}