package com.empty.jinux.simplediary.ui.settings

import com.empty.jinux.simplediary.data.backup.BackupModule
import com.empty.jinux.simplediary.data.source.diary.DiariesRepositoryModule
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [BackupModule::class, DiariesRepositoryModule::class])
internal interface SettingsFragmentComponent : AndroidInjector<SettingsFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<SettingsFragment>()
}