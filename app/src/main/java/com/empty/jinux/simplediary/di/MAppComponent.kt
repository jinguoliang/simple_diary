package com.empty.jinux.simplediary.di

import com.empty.jinux.simplediary.MApplication
import com.empty.jinux.simplediary.addeditdiary.AddEditDiaryActivity
import com.empty.jinux.simplediary.data.source.TasksRepositoryModule
import com.empty.jinux.simplediary.diarylist.DiaryListActivity
import com.empty.jinux.simplediary.statistics.StatisticsActivity
import com.empty.jinux.simplediary.taskdetail.TaskDetailActivityModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@dagger.Component(
        modules = arrayOf(AndroidSupportInjectionModule::class,
                AppModule::class, TasksRepositoryModule::class,
                AddEditDiaryActivity.Module::class,
                TaskDetailActivityModule::class,
                StatisticsActivity.Module::class,
                DiaryListActivity.Module::class)
)
interface MAppComponent : AndroidInjector<MApplication> {

    @dagger.Component.Builder
    abstract class Builder : AndroidInjector.Builder<MApplication>() {

    }
}