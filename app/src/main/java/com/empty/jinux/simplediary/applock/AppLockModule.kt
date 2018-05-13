package com.empty.jinux.simplediary.applock

import android.content.Context
import com.empty.jinux.simplediary.config.ConfigManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by jingu on 2018/2/23.
 *
 * location module
 */

@Module
class AppLockModule {

    @Provides
    @Singleton
    fun provide(context: Context, config: ConfigManager): AppLockManager {
        return AppLockImplement(context, config)
    }
}