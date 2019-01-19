package com.empty.jinux.simplediary.ui.main.diarylist

import androidx.fragment.app.Fragment
import dagger.Binds
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap

@dagger.Module(subcomponents = [(DiaryListFragmentComponent::class)])
internal abstract class DiaryListFragmentModule {

    @Binds
    @IntoMap
    @FragmentKey(DiaryListFragment::class)
    internal abstract fun bind(builder: DiaryListFragmentComponent.Builder): AndroidInjector.Factory<out androidx.fragment.app.Fragment>

    @Binds
    internal abstract fun bindView(v: DiaryListFragment): DiaryListContract.View
}