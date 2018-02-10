package com.empty.jinux.simplediary.di;

import com.empty.jinux.simplediary.MApplication
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@dagger.Component(
        modules = arrayOf(AndroidSupportInjectionModule::class, DogModule::class)
)
interface MAppComponent : AndroidInjector<MApplication> {

    @dagger.Component.Builder
    abstract class Builder : AndroidInjector.Builder<MApplication>() {

    }
}