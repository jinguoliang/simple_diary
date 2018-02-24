package com.empty.jinux.simplediary.location

import android.app.Activity
import com.empty.jinux.simplediary.taskdetail.TaskDetailActivity
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
    fun provideLocationManager(context: TaskDetailActivity): LocationManager {
        return LocationManagerImpl(context)
    }
}