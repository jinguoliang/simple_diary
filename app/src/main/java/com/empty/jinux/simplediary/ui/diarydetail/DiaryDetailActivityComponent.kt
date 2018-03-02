package com.empty.jinux.simplediary.ui.diarydetail

import com.empty.jinux.simplediary.ui.diarydetail.fragment.DiaryDetailFragmentModule
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent(modules = arrayOf(DiaryDetailFragmentModule::class))
internal interface DiaryDetailActivityComponent : AndroidInjector<DiaryDetailActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<DiaryDetailActivity>()
}