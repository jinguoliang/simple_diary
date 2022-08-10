package com.empty.jinux.simplediary.applock

import android.content.Context
import com.empty.jinux.simplediary.MApplication
import com.empty.jinux.simplediary.config.ConfigManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by jingu on 2018/2/23.
 *
 * location module
 */

@Module
@InstallIn(SingletonComponent::class)

class AppLockModule {

    @Provides
    @Singleton
    fun provide(@ApplicationContext context: Context, config: ConfigManager): AppLockManager {
        return AppLockImplement(context, config)
    }
}