package com.empty.jinux.simplediary

import com.empty.jinux.baselibaray.logd
import com.empty.jinux.simplediary.di.DaggerMAppComponent
import com.empty.jinux.simplediary.model.Dog
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import javax.inject.Inject

class MApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerMAppComponent.builder().create(this)
    }


    @Inject
    lateinit var dog: Dog

    override fun onCreate() {
        super.onCreate()
        logd(dog.name)
    }
}