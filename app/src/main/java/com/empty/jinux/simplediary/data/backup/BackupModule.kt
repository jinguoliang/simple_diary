package com.empty.jinux.simplediary.data.backup

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import com.empty.jinux.simplediary.di.Local
import com.empty.jinux.simplediary.di.Remote
import com.empty.jinux.simplediary.path.PathManager
import dagger.Module
import dagger.Provides


@Module
abstract class BackupModule {
    @Module
    companion object {

        @JvmStatic
        @Provides
        @Local
        fun provideLocalBackup(context: Context, pathManager: PathManager): Backup {
            return LocalBackup(context, pathManager)
        }

        @JvmStatic
        @Provides
        @Remote
        fun provideRemoteBackup(context: Activity, pathManager: PathManager): Backup {
            return GoogleDriverBackup(context, pathManager)
        }
    }
}