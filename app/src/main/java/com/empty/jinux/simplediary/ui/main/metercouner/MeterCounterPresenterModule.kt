package com.empty.jinux.simplediary.ui.main.metercouner

import dagger.Module
import dagger.Provides

@Module
class MeterCounterPresenterModule {

    @Provides
    fun providePresenter(v: MeterCounterContract.View
    ): MeterCounterContract.Presenter {
        return MeterCounterPresenter(v)
    }

}
