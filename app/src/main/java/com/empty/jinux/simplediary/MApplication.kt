package com.empty.jinux.simplediary

import android.net.Uri
import com.empty.jinux.baselibaray.loge
import com.empty.jinux.baselibaray.logi
import com.empty.jinux.baselibaray.logw
import com.empty.jinux.simplediary.di.DaggerMAppComponent
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import java.io.File


class MApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerMAppComponent.builder().create(this)
    }

}