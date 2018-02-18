package com.empty.jinux.simplediary.statistics

import dagger.Module
import dagger.Provides

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * [StatisticsPresenter].
 */
@Module
class StatisticsPresenterModule(private val mView: StatisticsContract.View) {

    @Provides
    internal fun provideStatisticsContractView(): StatisticsContract.View {
        return mView
    }
}
