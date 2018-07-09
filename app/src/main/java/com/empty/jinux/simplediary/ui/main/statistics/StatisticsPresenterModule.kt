package com.empty.jinux.simplediary.ui.main.statistics

import com.empty.jinux.simplediary.data.source.DiariesDataSource
import com.empty.jinux.simplediary.di.Repository
import com.empty.jinux.simplediary.report.Reporter
import dagger.Module
import dagger.Provides

/**
 * Created by jingu on 2018/2/24.
 *
 */

@Module
class StatisticsPresenterModule {
    @Provides
    fun providePresenter(
            @Repository repo: DiariesDataSource,
            v: StatisticsContract.View
    ): StatisticsContract.Presenter {
        return StatisticsPresenter(repo, v)
    }
}