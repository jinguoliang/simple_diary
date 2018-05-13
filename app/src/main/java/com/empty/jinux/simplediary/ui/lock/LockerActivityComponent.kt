package com.empty.jinux.simplediary.ui.lock

import com.empty.jinux.simplediary.applock.AppLockModule
import com.empty.jinux.simplediary.ui.diarydetail.fragment.DiaryDetailFragmentModule
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent()
internal interface LockerActivityComponent : AndroidInjector<LockActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<LockActivity>()
}