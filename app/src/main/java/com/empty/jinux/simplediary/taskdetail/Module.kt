package com.empty.jinux.simplediary.taskdetail

import android.app.Activity
import dagger.Binds
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@dagger.Module(subcomponents = arrayOf(Component::class))
internal abstract class Module {

    @Binds
    @IntoMap
    @ActivityKey(TaskDetailActivity::class)
    internal abstract fun bind(builder: Component.Builder): AndroidInjector.Factory<out Activity>
}