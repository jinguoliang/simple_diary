package com.empty.jinux.simplediary.ui.main.diarylist

import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = [(DiaryListPresenterModule::class)])
internal interface DiaryListFragmentComponent : AndroidInjector<DiaryListFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<DiaryListFragment>()
}