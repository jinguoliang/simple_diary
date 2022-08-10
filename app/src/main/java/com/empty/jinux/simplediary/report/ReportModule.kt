package com.empty.jinux.simplediary.report

import android.content.Context
import com.empty.jinux.simplediary.MApplication
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by jingu on 2018/2/23.
 *
 * location module
 */

@Module
@InstallIn(SingletonComponent::class)

class ReportModule {
    @Provides
    fun provideReporter(@ApplicationContext context: Context): Reporter {
        return FirebaseReporter(context)
    }
}