package com.empty.jinux.simplediary.data.source

import android.content.Context
import com.empty.jinux.simplediary.data.source.local.DiariesLocalDataSource
import com.empty.jinux.simplediary.data.source.remote.DiariesRemoteDataSource


import dagger.Module
import dagger.Provides

/**
 * This is used by Dagger to inject the required arguments into the [DiariesRepository].
 */
@Module
class DiariesRepositoryModule {

    @Provides
    @Local
    internal fun provideTasksLocalDataSource(context: Context): DiariesDataSource {
        return DiariesLocalDataSource(context)
    }

    @Provides
    @Remote
    internal fun provideTasksRemoteDataSource(): DiariesDataSource {
        return DiariesRemoteDataSource()
    }

}
