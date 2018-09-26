package com.empty.jinux.simplediary.data.backup

import androidx.fragment.app.Fragment
import com.empty.jinux.simplediary.di.Local
import com.empty.jinux.simplediary.di.Remote
import dagger.Module
import dagger.Provides


@Module
abstract class BackupModule {
    @Module
    companion object {

        @JvmStatic
        @Provides
        @Local
        fun provideLocalBackup(context: Fragment): Backup {
            return LocalBackup(context)
        }

        @JvmStatic
        @Provides
        @Remote
        fun provideRemoteBackup(context: Fragment): Backup {
            return GoogleDriverBackup(context)
        }
    }
}