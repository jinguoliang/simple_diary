package com.empty.jinux.simplediary.ui.settings

import android.app.Activity
import androidx.fragment.app.Fragment
import dagger.Binds
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap

@dagger.Module(subcomponents = arrayOf(SettingsFragmentComponent::class))
internal abstract class SettingsFragmentModule {

    @Binds
    @IntoMap
    @FragmentKey(SettingsFragment::class)
    internal abstract fun bind(builder: SettingsFragmentComponent.Builder): AndroidInjector.Factory<out androidx.fragment.app.Fragment>

    @Binds
    internal abstract fun bindView(v: SettingsFragment): Fragment

    @Binds
    internal abstract fun bindActivity(v: SettingsActivity): Activity
}
