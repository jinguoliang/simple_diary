package com.empty.jinux.simplediary.ui.main.diarylist

import com.empty.jinux.simplediary.data.source.DiariesDataSource
import com.empty.jinux.simplediary.di.Repository
import dagger.Module
import dagger.Provides

@Module
class DiaryListPresenterModule {

    @Provides
    fun providePresenter(
            @Repository repo: DiariesDataSource,
            v: DiaryListContract.View
    ): DiaryListContract.Presenter {
        return DiaryListPresenter(repo, v)
    }

}
