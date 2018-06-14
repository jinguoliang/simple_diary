package com.empty.jinux.simplediary.data.backup

import android.app.Activity
import android.support.v4.app.Fragment
import com.empty.jinux.simplediary.data.source.DiariesDataSource
import com.empty.jinux.simplediary.data.source.DiariesRepository
import com.empty.jinux.simplediary.di.Dang
import com.empty.jinux.simplediary.di.Local
import com.empty.jinux.simplediary.di.Remote
import com.empty.jinux.simplediary.di.Repository
import dagger.Binds
import dagger.Module
import dagger.Provides


@Module
abstract class BackupModule {
    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideLocalBackup(context: Fragment): Backup {
            return LocalBackup(context)
        }

        @JvmStatic
        @Provides
        @Remote
        fun provideRemoteBackup(context: Fragment): Backup {
            return GoogleDriverBackup(context)
        }

        @JvmStatic
        @Provides
        @Local
        fun provideRemoteBackup1(context: Fragment): Backup {
            return GoogleDriverBackup(context)
        }


    }
}