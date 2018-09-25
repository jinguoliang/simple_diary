package com.empty.jinux.simplediary

import com.empty.jinux.simplediary.di.DaggerMAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.multimoon.colorful.Defaults
import io.multimoon.colorful.ThemeColor
import io.multimoon.colorful.initColorful


class MApplication : MDaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out MDaggerApplication> {
        return DaggerMAppComponent.builder().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        val defaults: Defaults = Defaults(
                primaryColor = ThemeColor.GREEN,
                accentColor = ThemeColor.BLUE,
                useDarkTheme = false,
                translucent = false)
        initColorful(this, defaults)
    }

}

