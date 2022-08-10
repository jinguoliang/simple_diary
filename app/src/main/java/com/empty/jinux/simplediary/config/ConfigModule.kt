package com.empty.jinux.simplediary.config

import android.content.Context
import com.empty.jinux.simplediary.MApplication
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
class ConfigModule {

    @Provides
    @Singleton
    fun provide(@ApplicationContext context: Context): ConfigManager {
        return SharePreferenceConfig(context)
    }
}