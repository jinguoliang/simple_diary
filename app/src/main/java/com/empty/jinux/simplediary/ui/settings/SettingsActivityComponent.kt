package com.empty.jinux.simplediary.ui.settings

import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [(SettingsFragmentModule::class)])
internal interface SettingsActivityComponent : AndroidInjector<SettingsActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<SettingsActivity>()
}