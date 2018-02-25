package com.empty.jinux.simplediary.ui.taskdetail.presenter

import com.empty.jinux.simplediary.data.source.TasksRepository
import com.empty.jinux.simplediary.location.LocationManager
import com.empty.jinux.simplediary.ui.taskdetail.TaskDetailContract
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
            repo: TasksRepository,
            v: TaskDetailContract.View,
            locationManager: LocationManager,
            weatherManager: WeatherManager
    ): TaskDetailContract.Presenter {
        return TaskDetailPresenter(repo, v, locationManager, weatherManager)
    }
}