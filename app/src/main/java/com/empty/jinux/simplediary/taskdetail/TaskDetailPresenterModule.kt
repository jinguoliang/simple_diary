package com.empty.jinux.simplediary.taskdetail

import dagger.Module
import dagger.Provides

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * [TaskDetailPresenter].
 */
@Module
class TaskDetailPresenterModule(private val mView: TaskDetailContract.View, private val mTaskId: String) {

    @Provides
    internal fun provideTaskDetailContractView(): TaskDetailContract.View {
        return mView
    }

    @Provides
    internal fun provideTaskId(): String {
        return mTaskId
    }
}
