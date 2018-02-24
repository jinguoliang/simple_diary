package com.empty.jinux.simplediary.taskdetail.fragment

import com.empty.jinux.simplediary.location.LocationModule
import com.empty.jinux.simplediary.taskdetail.presenter.TaskDetailPresenterModule
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = arrayOf(LocationModule::class, TaskDetailPresenterModule::class))
internal interface DiaryDetailFragmentComponent : AndroidInjector<TaskDetailFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<TaskDetailFragment>()
}