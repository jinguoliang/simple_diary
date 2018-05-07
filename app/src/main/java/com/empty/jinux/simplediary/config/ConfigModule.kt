package com.empty.jinux.simplediary.config

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by jingu on 2018/2/23.
 *
 * location module
 */

@Module
class ConfigModule {

    @Provides
    @Singleton
    fun provide(context: Context): ConfigManager {
        return SharePreferenceConfig(context)
    }
}