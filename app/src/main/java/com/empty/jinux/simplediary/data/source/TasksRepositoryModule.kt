package com.empty.jinux.simplediary.data.source

import android.content.Context
import com.empty.jinux.simplediary.data.source.local.TasksLocalDataSource
import com.empty.jinux.simplediary.data.source.remote.DiariesRemoteDataSource


import dagger.Module
import dagger.Provides

/**
 * This is used by Dagger to inject the required arguments into the [TasksRepository].
 */
@Module
class TasksRepositoryModule {

    @Provides
    @Local
    internal fun provideTasksLocalDataSource(context: Context): TasksDataSource {
        return TasksLocalDataSource(context)
    }

    @Provides
    @Remote
    internal fun provideTasksRemoteDataSource(): TasksDataSource {
        return DiariesRemoteDataSource()
    }

}
