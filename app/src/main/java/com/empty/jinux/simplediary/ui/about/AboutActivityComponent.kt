package com.empty.jinux.simplediary.ui.about

import com.empty.jinux.simplediary.applock.AppLockModule
import com.empty.jinux.simplediary.ui.diarydetail.fragment.DiaryDetailFragmentModule
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent()
internal interface AboutActivityComponent : AndroidInjector<AboutActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<AboutActivity>()
}