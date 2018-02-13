package com.empty.jinux.simplediary.diarylist

import dagger.Module
import dagger.Provides

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * [DiaryListPresenter].
 */
@Module
class DiaryListPresenterModule(private val mView: DiaryListContract.View) {

    @Provides
    internal fun provideTasksContractView(): DiaryListContract.View {
        return mView
    }

}
