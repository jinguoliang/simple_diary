package com.empty.jinux.simplediary.di

import android.content.Context

import com.empty.jinux.simplediary.MApplication

import dagger.Binds
import dagger.Module

@Module
abstract class AppModule {
    @Binds
    abstract fun provideContext(application: MApplication): Context
}