package com.empty.jinux.simplediary.ui.main.metercouner

import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [MeterCounterPresenterModule::class])
internal interface MeterCounterFragmentComponent : AndroidInjector<MeterCounterFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MeterCounterFragment>()
}