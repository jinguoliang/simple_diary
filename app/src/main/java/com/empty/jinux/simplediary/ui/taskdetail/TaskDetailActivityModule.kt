package com.empty.jinux.simplediary.ui.taskdetail

import android.app.Activity
import dagger.Binds
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@dagger.Module(subcomponents = arrayOf(TaskDetailActivityComponent::class))
internal abstract class TaskDetailActivityModule {

    @Binds
    @IntoMap
    @ActivityKey(TaskDetailActivity::class)
    internal abstract fun bind(builder: TaskDetailActivityComponent.Builder): AndroidInjector.Factory<out Activity>
}