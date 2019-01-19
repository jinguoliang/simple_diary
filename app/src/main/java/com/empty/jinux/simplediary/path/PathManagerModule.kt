package com.empty.jinux.simplediary.path

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class PathManagerModule {
    @Provides
    fun provider(context: Context): PathManager {
        return AndroidPathManager(context)
    }
}