package com.empty.jinux.simplediary.ui.diarydetail.presenter

import androidx.fragment.app.Fragment
import com.empty.jinux.simplediary.data.source.DiariesDataSource
import com.empty.jinux.simplediary.di.Repository
import com.empty.jinux.simplediary.location.LocationManager
import com.empty.jinux.simplediary.report.Reporter
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailContract
import com.empty.jinux.simplediary.weather.WeatherManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

/**
 * Created by jingu on 2018/2/24.
 */

@Module
@InstallIn(FragmentComponent::class)
class TaskDetailPresenterModule {
    @Provides
    fun providePresenter(
        @Repository repo: DiariesDataSource,
        view: Fragment,
        locationManager: LocationManager,
        weatherManager: WeatherManager,
        reporter: Reporter
    ): DiaryDetailPresenter {
        return DiaryDetailPresenter(
            repo,
            view as DiaryDetailContract.View,
            locationManager,
            weatherManager,
            reporter
        )
    }
}