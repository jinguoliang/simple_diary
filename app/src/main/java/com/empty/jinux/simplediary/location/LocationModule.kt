package com.empty.jinux.simplediary.location

import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailActivity
import dagger.Module
import dagger.Provides

/**
 * Created by jingu on 2018/2/23.
 *
 * location module
 */

@Module
class LocationModule {

    @Provides
    fun provideLocationManager(context: DiaryDetailActivity): LocationManager {
        return LocationManagerImpl(context)
    }
}