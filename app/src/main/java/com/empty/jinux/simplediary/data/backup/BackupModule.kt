package com.empty.jinux.simplediary.data.backup

import androidx.fragment.app.Fragment
import com.empty.jinux.simplediary.MApplication
import com.empty.jinux.simplediary.di.Local
import com.empty.jinux.simplediary.di.Remote
import com.empty.jinux.simplediary.ui.settings.SettingsActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent


@Module
@InstallIn(FragmentComponent::class)
abstract class BackupModule {
    companion object {

        @JvmStatic
        @Provides
        @Local
        fun provideLocalBackup(context: Fragment): Backup {
            return LocalBackup(context)
        }
    }
}