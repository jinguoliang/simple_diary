package com.empty.jinux.simplediary

import com.empty.jinux.simplediary.di.DaggerMAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication


class MApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerMAppComponent.builder().create(this)
    }

}