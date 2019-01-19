package com.empty.jinux.simplediary.ui.main.metercouner

import dagger.Binds
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap

@dagger.Module(subcomponents = [(MeterCounterFragmentComponent::class)])
internal abstract class MeterCounterFragmentModule {

    @Binds
    @IntoMap
    @FragmentKey(MeterCounterFragment::class)
    internal abstract fun bind(builder: MeterCounterFragmentComponent.Builder): AndroidInjector.Factory<out androidx.fragment.app.Fragment>

    @Binds
    internal abstract fun bindView(v: MeterCounterFragment): MeterCounterContract.View
}