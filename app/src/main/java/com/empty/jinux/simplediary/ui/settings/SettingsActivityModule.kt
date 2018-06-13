package com.empty.jinux.simplediary.ui.settings

import android.app.Activity
import com.empty.jinux.simplediary.ui.main.diarylist.DiaryListContract
import com.empty.jinux.simplediary.ui.main.diarylist.DiaryListFragment
import dagger.Binds
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@dagger.Module(subcomponents = [(SettingsActivityComponent::class)])
internal abstract class SettingsActivityModule {

    @Binds
    @IntoMap
    @ActivityKey(SettingsActivity::class)
    internal abstract fun bind(builder: SettingsActivityComponent.Builder): AndroidInjector.Factory<out Activity>
}