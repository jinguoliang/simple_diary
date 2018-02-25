package com.empty.jinux.simplediary.weather

import dagger.Binds
import dagger.Module

/**
 * Created by jingu on 2018/2/25.
 */

@Module
interface WeatherModule {

    @Binds
    fun bindWeatherApi(api: WeatherManagerRetrofitImpl): WeatherManager
}