package com.empty.jinux.simplediary.report

import android.content.Context
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailActivity
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by jingu on 2018/2/23.
 *
 * location module
 */

@Module
class ReportModule {

    @Provides
    @Singleton
    fun provideReporter(context: Context): Reporter {
        return FirebaseReporter(context)
    }
}