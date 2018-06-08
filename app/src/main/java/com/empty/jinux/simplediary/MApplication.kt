package com.empty.jinux.simplediary

import com.empty.jinux.simplediary.di.DaggerMAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication


class MApplication : MDaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out MDaggerApplication> {
        return DaggerMAppComponent.builder().create(this)
    }

}

