package com.empty.jinux.simplediary.di

import com.empty.jinux.simplediary.model.Dog
import dagger.Module
import dagger.Provides

@Module
class DogModule {
    @Provides
    fun provideDog(): Dog {
        return Dog("Wang")
    }
}