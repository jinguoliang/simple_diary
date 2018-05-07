package com.empty.jinux.simplediary.ui.lock

import android.app.Activity
import com.empty.jinux.simplediary.applock.AppLockModule
import dagger.Binds
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@dagger.Module(subcomponents = arrayOf(LockerActivityComponent::class))
internal abstract class LockerActivityModule {

    @Binds
    @IntoMap
    @ActivityKey(LockActivity::class)
    internal abstract fun bind(builder: LockerActivityComponent.Builder): AndroidInjector.Factory<out Activity>
}