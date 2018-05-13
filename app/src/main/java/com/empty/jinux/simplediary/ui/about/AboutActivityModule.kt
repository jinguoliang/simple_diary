package com.empty.jinux.simplediary.ui.about

import android.app.Activity
import com.empty.jinux.simplediary.applock.AppLockModule
import dagger.Binds
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@dagger.Module(subcomponents = arrayOf(AboutActivityComponent::class))
internal abstract class AboutActivityModule {

    @Binds
    @IntoMap
    @ActivityKey(AboutActivity::class)
    internal abstract fun bind(builder: AboutActivityComponent.Builder): AndroidInjector.Factory<out Activity>
}