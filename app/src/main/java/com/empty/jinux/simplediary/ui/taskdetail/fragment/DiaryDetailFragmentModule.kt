package com.empty.jinux.simplediary.ui.taskdetail.fragment

import android.support.v4.app.Fragment
import com.empty.jinux.simplediary.ui.taskdetail.TaskDetailContract
import dagger.Binds
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@dagger.Module(subcomponents = arrayOf(DiaryDetailFragmentComponent::class))
internal abstract class DiaryDetailFragmentModule {

    @Binds
    @IntoMap
    @dagger.android.support.FragmentKey(TaskDetailFragment::class)
    internal abstract fun bind(builder: DiaryDetailFragmentComponent.Builder): AndroidInjector.Factory<out Fragment>

    @Binds
    internal abstract fun bindView(v: TaskDetailFragment): TaskDetailContract.View
}