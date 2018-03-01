package com.empty.jinux.simplediary.di

import com.empty.jinux.simplediary.MApplication
import com.empty.jinux.simplediary.data.source.TasksRepositoryModule
import com.empty.jinux.simplediary.ui.diarylist.DiaryListActivity
import com.empty.jinux.simplediary.ui.statistics.StatisticsActivity
import com.empty.jinux.simplediary.ui.taskdetail.TaskDetailActivityModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@dagger.Component(
        modules = arrayOf(AndroidSupportInjectionModule::class,
                AppModule::class, TasksRepositoryModule::class,
                TaskDetailActivityModule::class,
                StatisticsActivity.Module::class,
                DiaryListActivity.Module::class)
)
interface MAppComponent : AndroidInjector<MApplication> {

    @dagger.Component.Builder
    abstract class Builder : AndroidInjector.Builder<MApplication>() {

    }
}