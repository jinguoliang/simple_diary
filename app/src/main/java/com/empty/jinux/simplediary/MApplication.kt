package com.empty.jinux.simplediary

import android.net.Uri
import com.empty.jinux.baselibaray.loge
import com.empty.jinux.baselibaray.logi
import com.empty.jinux.baselibaray.logw
import com.empty.jinux.simplediary.di.DaggerMAppComponent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import java.io.File
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class MApplication : DaggerApplication() {
    private lateinit var mStorageRef: StorageReference

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerMAppComponent.builder().create(this)
    }

    override fun onCreate() {
        super.onCreate()

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")
        myRef.setValue("Hello, World!")


        val mAuth = FirebaseAuth.getInstance()
        mAuth.signInAnonymously().addOnCompleteListener {
            if (it.isSuccessful) {
                logw("login successfully")

                logw(mAuth.currentUser?.displayName.toString())

                mStorageRef = FirebaseStorage.getInstance().getReference();
                upload()
            } else {
                loge("login failed")
            }
        }


//
//        val localFile = File.createTempFile("images", "jpg")
//        riversRef.getFile(localFile)
//                .addOnSuccessListener {
//                    // Successfully downloaded data to local file
//                    // ...
//                }.addOnFailureListener {
//            // Handle failed download
//            // ...
//        }
    }

    private fun upload() {
        val file = Uri.fromFile(File("/sdcard/temp.jpg"))
        val riversRef = mStorageRef.child("rivers.jpg")

        riversRef.putFile(file)
                .addOnSuccessListener({ taskSnapshot ->
                    // Get a URL to the uploaded content
                    val downloadUrl = taskSnapshot.downloadUrl
                    logi("the url = $downloadUrl")
                })
                .addOnFailureListener({
                    // Handle unsuccessful uploads
                    // ...
                    loge("upload file error")
                })

    }

}