package com.empty.jinux.simplediary.ui.diarydetail.fragment

import com.empty.jinux.simplediary.location.LocationModule
import com.empty.jinux.simplediary.report.ReportModule
import com.empty.jinux.simplediary.report.Reporter
import com.empty.jinux.simplediary.ui.diarydetail.presenter.TaskDetailPresenterModule
import com.empty.jinux.simplediary.weather.WeatherModule
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = arrayOf(
        LocationModule::class,
        TaskDetailPresenterModule::class,
        WeatherModule::class,
        ReportModule::class
))
internal interface DiaryDetailFragmentComponent : AndroidInjector<DiaryDetailFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<DiaryDetailFragment>()
}