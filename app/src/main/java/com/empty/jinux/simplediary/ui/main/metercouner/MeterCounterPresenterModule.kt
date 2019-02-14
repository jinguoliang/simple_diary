package com.empty.jinux.simplediary.ui.main.metercouner

import com.empty.jinux.simplediary.data.source.metercounter.MeterCounterDataSource
import dagger.Module
import dagger.Provides

@Module
class MeterCounterPresenterModule {

    @Provides
    fun providePresenter(source: MeterCounterDataSource, v: MeterCounterContract.View
    ): MeterCounterContract.Presenter {
        return MeterCounterPresenter(source, v)
    }

}
