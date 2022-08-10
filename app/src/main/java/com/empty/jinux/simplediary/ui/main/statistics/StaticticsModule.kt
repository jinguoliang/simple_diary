package com.empty.jinux.simplediary.ui.main.statistics

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.empty.jinux.simplediary.MApplication
import com.empty.jinux.simplediary.data.source.DiariesDataSource
import com.empty.jinux.simplediary.di.Local
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext

/**
 * Created by jingu on 2018/2/23.
 *
 * location module
 */

@Module
@InstallIn(FragmentComponent::class)
object StaticticsModule {

    @Provides
    fun provideStaticsticsPresenter(@Local datasource: DiariesDataSource, context: Fragment): StatisticsContract.Presenter {
        return StatisticsPresenter(datasource, context as StatisticsContract.View)
    }
}