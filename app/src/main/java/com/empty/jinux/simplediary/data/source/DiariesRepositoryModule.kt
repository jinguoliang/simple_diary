package com.empty.jinux.simplediary.data.source


import android.content.Context
import com.empty.jinux.simplediary.data.source.local.DiariesLocalDataSource
import com.empty.jinux.simplediary.data.source.remote.DiariesRemoteDataSource
import com.empty.jinux.simplediary.di.EmptyData
import com.empty.jinux.simplediary.di.Local
import com.empty.jinux.simplediary.di.Remote
import com.empty.jinux.simplediary.di.Repository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

/**
 * This is used by Dagger to inject the required arguments into the [DiariesRepository].
 */
@Module
@InstallIn(ActivityComponent::class)
abstract class DiariesRepositoryModule {

    @Repository
    @Binds
    abstract fun bindsDiariesRepositoryDataSource(repository: DiariesRepository): DiariesDataSource

}

@Module
@InstallIn(SingletonComponent::class)
object DiariesRepositoryModule1 {
    @Provides
    @Local
    internal fun provideDiariesLocalDataSource(@ApplicationContext context: Context): DiariesDataSource {
        return DiariesLocalDataSource(context)
    }

    @Provides
    @Remote
    fun provideDiariesRemoteDataSource(): DiariesDataSource {
        return DiariesRemoteDataSource()
    }

    @Provides
    @EmptyData
    fun provideDiariesEmptyDataSource(): DiariesDataSource {
        return EmptyDataSource()
    }
}
