package com.empty.jinux.simplediary.weather

import com.empty.jinux.simplediary.MApplication
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailActivity
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

/**
 * Created by jingu on 2018/2/25.
 */

@Module
@InstallIn(ActivityComponent::class)
interface WeatherModule {

    @Binds
    fun bindWeatherApi(api: WeatherManagerRetrofitImpl): WeatherManager
}