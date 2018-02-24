package com.empty.jinux.simplediary.taskdetail

import com.empty.jinux.simplediary.location.LocationModule
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = arrayOf(LocationModule::class))
internal interface Component : AndroidInjector<TaskDetailActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<TaskDetailActivity>()
}