package com.empty.jinux.simplediary.ui.diarydetail.fragment

import androidx.fragment.app.Fragment
import com.empty.jinux.simplediary.ui.diarydetail.DiaryDetailContract
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap

@Module(subcomponents = arrayOf(DiaryDetailFragmentComponent::class))
internal abstract class DiaryDetailFragmentModule {

    @Binds
    @IntoMap
    @FragmentKey(DiaryDetailFragment::class)
    internal abstract fun bind(builder: DiaryDetailFragmentComponent.Builder): AndroidInjector.Factory<out androidx.fragment.app.Fragment>

    @Binds
    internal abstract fun bindView(v: DiaryDetailFragment): DiaryDetailContract.View
}