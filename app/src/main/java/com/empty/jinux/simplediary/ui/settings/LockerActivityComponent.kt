package com.empty.jinux.simplediary.ui.settings

import com.empty.jinux.simplediary.applock.AppLockModule
import com.empty.jinux.simplediary.ui.diarydetail.fragment.DiaryDetailFragmentModule
import com.empty.jinux.simplediary.ui.settings.SettingsActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent()
internal interface SettingsActivityComponent : AndroidInjector<SettingsActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<SettingsActivity>()
}