package com.empty.jinux.simplediary.addedittask

import dagger.Module
import dagger.Provides

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * [AddEditTaskPresenter].
 */
@Module
class AddEditTaskPresenterModule(private val mView: AddEditTaskContract.View, private val mTaskId: String?) {

    @Provides
    fun provideAddEditTaskContractView(): AddEditTaskContract.View {
        return mView
    }

    @Provides
    internal fun provideTaskId(): String? {
        return mTaskId
    }
}
