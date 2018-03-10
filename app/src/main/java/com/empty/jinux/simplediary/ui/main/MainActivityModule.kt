package com.empty.jinux.simplediary.ui.main

import android.app.Activity
import dagger.Binds
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@dagger.Module(subcomponents = arrayOf(MainActivityComponent::class))
internal abstract class MainActivityModule {

    @Binds
    @IntoMap
    @ActivityKey(MainActivity::class)
    internal abstract fun bind(builder: MainActivityComponent.Builder): AndroidInjector.Factory<out Activity>
}