package com.empty.jinux.simplediary.ui.diarydetail.fragment

import android.support.v4.app.Fragment
import com.empty.jinux.simplediary.ui.main.statistics.StatisticsContract
import com.empty.jinux.simplediary.ui.main.statistics.StatisticsFragment
import com.empty.jinux.simplediary.ui.main.statistics.StatisticsFragmentComponent
import dagger.Binds
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@dagger.Module(subcomponents = arrayOf(StatisticsFragmentComponent::class))
internal abstract class StatisticsFragmentModule {

    @Binds
    @IntoMap
    @dagger.android.support.FragmentKey(StatisticsFragment::class)
    internal abstract fun bind(builder: StatisticsFragmentComponent.Builder): AndroidInjector.Factory<out Fragment>

    @Binds
    internal abstract fun bindView(v: StatisticsFragment): StatisticsContract.View
}