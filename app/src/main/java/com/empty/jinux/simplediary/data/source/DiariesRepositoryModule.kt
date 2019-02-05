package com.empty.jinux.simplediary.data.source


import android.content.Context
import com.empty.jinux.simplediary.data.metercounter.MeterCounterLocalSource
import com.empty.jinux.simplediary.data.metercounter.MeterCounterDataSource
import com.empty.jinux.simplediary.data.source.local.DiariesLocalDataSource
import com.empty.jinux.simplediary.data.source.remote.DiariesRemoteDataSource
import com.empty.jinux.simplediary.di.EmptyData
import com.empty.jinux.simplediary.di.Local
import com.empty.jinux.simplediary.di.Remote
import com.empty.jinux.simplediary.di.Repository
import dagger.Binds
import dagger.Module
import dagger.Provides

/**
 * This is used by Dagger to inject the required arguments into the [DiariesRepository].
 */
@Module
abstract class DiariesRepositoryModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        @Local
        internal fun provideDiariesLocalDataSource(context: Context): DiariesDataSource {
            return DiariesLocalDataSource(context)
        }

        @JvmStatic
        @Provides
        @Remote
        internal fun provideDiariesRemoteDataSource(): DiariesDataSource {
            return DiariesRemoteDataSource()
        }

        @JvmStatic
        @Provides
        @EmptyData
        internal fun provideDiariesEmptyDataSource(): DiariesDataSource {
            return EmptyDataSource()
        }

        @JvmStatic
        @Provides
        internal fun provideMeterCounterLocalDataSource(context: Context): MeterCounterDataSource {
            return MeterCounterLocalSource(context)
        }
    }


    @Repository
    @Binds
    abstract fun bindsDiariesRepositoryDataSource(repository: DiariesRepository): DiariesDataSource

}
