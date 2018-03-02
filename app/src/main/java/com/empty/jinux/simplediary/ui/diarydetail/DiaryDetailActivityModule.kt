package com.empty.jinux.simplediary.ui.diarydetail

import android.app.Activity
import dagger.Binds
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@dagger.Module(subcomponents = arrayOf(DiaryDetailActivityComponent::class))
internal abstract class DiaryDetailActivityModule {

    @Binds
    @IntoMap
    @ActivityKey(DiaryDetailActivity::class)
    internal abstract fun bind(builder: DiaryDetailActivityComponent.Builder): AndroidInjector.Factory<out Activity>
}