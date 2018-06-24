package com.empty.jinux.simplediary.ui.settings

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap

@dagger.Module(subcomponents = arrayOf(SettingsFragmentComponent::class))
internal abstract class SettingsFragmentModule {

    @Binds
    @IntoMap
    @FragmentKey(SettingsFragment::class)
    internal abstract fun bind(builder: SettingsFragmentComponent.Builder): AndroidInjector.Factory<out Fragment>

    @Binds
    internal abstract fun bindView(v: SettingsFragment): Fragment
}