package com.empty.jinux.simplediary.location

import android.app.Activity
import com.empty.jinux.simplediary.MApplication
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

/**
 * Created by jingu on 2018/2/23.
 *
 * location module
 */

@Module
@InstallIn(ActivityComponent::class)
class LocationModule {

    @Provides
    fun provideLocationManager(context: Activity): LocationManager {
        return LocationManagerImpl(context)
    }
}