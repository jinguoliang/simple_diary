package com.empty.jinux.simplediary.ui.addeditdiary

import dagger.Module
import dagger.Provides

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * [AddEditDiaryPresenter].
 */
@Module
class AddEditDiaryPresenterModule(private val mView: AddEditDiaryContract.View, private val mTaskId: String?) {

    @Provides
    fun provideAddEditTaskContractView(): AddEditDiaryContract.View {
        return mView
    }

    @Provides
    internal fun provideTaskId(): String? {
        return mTaskId
    }
}
