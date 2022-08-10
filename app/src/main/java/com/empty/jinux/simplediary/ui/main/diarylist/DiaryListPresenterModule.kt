package com.empty.jinux.simplediary.ui.main.diarylist

import androidx.fragment.app.Fragment
import com.empty.jinux.simplediary.MApplication
import com.empty.jinux.simplediary.data.source.DiariesDataSource
import com.empty.jinux.simplediary.di.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
class DiaryListPresenterModule {

    @Provides
    fun providePresenter(
            @Repository repo: DiariesDataSource,
            v: Fragment
    ): DiaryListContract.Presenter {
        return DiaryListPresenter(repo, v as DiaryListContract.View)
    }
}

