package com.empty.jinux.simplediary.ui.diarydetail.presenter

import com.empty.jinux.simplediary.data.source.DiariesRepository
import com.empty.jinux.simplediary.location.LocationManager
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailContract
import com.empty.jinux.simplediary.weather.WeatherManager
import dagger.Module
import dagger.Provides

/**
 * Created by jingu on 2018/2/24.
 */

@Module
class TaskDetailPresenterModule {
    @Provides
    fun providePresenter(
            repo: DiariesRepository,
            v: DiaryDetailContract.View,
            locationManager: LocationManager,
            weatherManager: WeatherManager
    ): DiaryDetailContract.Presenter {
        return DiaryDetailPresenter(repo, v, locationManager, weatherManager)
    }
}