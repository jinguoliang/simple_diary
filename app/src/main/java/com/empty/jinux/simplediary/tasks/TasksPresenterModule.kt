package com.empty.jinux.simplediary.tasks

import dagger.Module
import dagger.Provides

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * [TasksPresenter].
 */
@Module
class TasksPresenterModule(private val mView: TasksContract.View) {

    @Provides
    internal fun provideTasksContractView(): TasksContract.View {
        return mView
    }

}
