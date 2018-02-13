package com.empty.jinux.simplediary.di

import com.empty.jinux.simplediary.MApplication
import com.empty.jinux.simplediary.addedittask.AddEditTaskActivity
import com.empty.jinux.simplediary.data.source.TasksRepositoryModule
import com.empty.jinux.simplediary.diarylist.DiaryListActivity
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@dagger.Component(
        modules = arrayOf(AndroidSupportInjectionModule::class, DogModule::class,
                AppModule::class, TasksRepositoryModule::class,
                AddEditTaskActivity.Module::class,
                DiaryListActivity.Module::class)
)
interface MAppComponent : AndroidInjector<MApplication> {

    @dagger.Component.Builder
    abstract class Builder : AndroidInjector.Builder<MApplication>() {

    }
}