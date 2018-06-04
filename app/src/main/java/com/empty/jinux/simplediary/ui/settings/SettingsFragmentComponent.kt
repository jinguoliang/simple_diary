package com.empty.jinux.simplediary.ui.settings

import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent()
internal interface SettingsFragmentComponent : AndroidInjector<SettingsFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<SettingsFragment>()
}