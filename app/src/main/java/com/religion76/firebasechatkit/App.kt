package com.religion76.firebasechatkit

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

/**
 * Created by SunChao on 2018/8/2.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("Firebase", "initializeApp")
        FirebaseApp.initializeApp(this)
    }
}