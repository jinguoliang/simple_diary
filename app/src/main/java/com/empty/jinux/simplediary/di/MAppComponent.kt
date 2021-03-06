package com.empty.jinux.simplediary.di

import com.empty.jinux.simplediary.MApplication
import com.empty.jinux.simplediary.applock.AppLockModule
import com.empty.jinux.simplediary.config.ConfigModule
import com.empty.jinux.simplediary.data.source.DiariesRepositoryModule
import com.empty.jinux.simplediary.report.ReportModule
import com.empty.jinux.simplediary.ui.about.AboutActivityModule
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailActivityModule
import com.empty.jinux.simplediary.ui.lock.LockerActivityModule
import com.empty.jinux.simplediary.ui.main.MainActivityModule
import com.empty.jinux.simplediary.ui.settings.SettingsActivityModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@dagger.Component(
        modules = arrayOf(AndroidSupportInjectionModule::class,
                AppModule::class,
                DiariesRepositoryModule::class,
                DiaryDetailActivityModule::class,
                MainActivityModule::class,
                LockerActivityModule::class,
                AboutActivityModule::class,
                SettingsActivityModule::class,
                ConfigModule::class,
                AppLockModule::class,
                ReportModule::class)
)
interface MAppComponent : AndroidInjector<MApplication> {

    @dagger.Component.Builder
    abstract class Builder : AndroidInjector.Builder<MApplication>()
}