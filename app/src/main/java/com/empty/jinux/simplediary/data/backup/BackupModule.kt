package com.empty.jinux.simplediary.data.backup

import android.support.v4.app.Fragment
import dagger.Module
import dagger.Provides


@Module
abstract class BackupModule {
    @Module
    companion object {
//        @JvmStatic
//        @Provides
//        fun provideLocalBackup(context: Activity): Backup {
//            return LocalBackup(context)
//        }

        @JvmStatic
        @Provides
        fun provideRemoteBackup(context: Fragment): Backup {
            return GoogleDriverBackup(context)
        }
    }
}