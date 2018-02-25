package com.empty.jinux.simplediary.ui.taskdetail

import com.empty.jinux.simplediary.ui.taskdetail.fragment.DiaryDetailFragmentModule
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = arrayOf(DiaryDetailFragmentModule::class))
internal interface TaskDetailActivityComponent : AndroidInjector<TaskDetailActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<TaskDetailActivity>()
}