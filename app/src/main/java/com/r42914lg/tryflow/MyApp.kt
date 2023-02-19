package com.r42914lg.tryflow

import android.app.Application
import com.r42914lg.tryflow.utils.log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        log("App created")
    }

    companion object {
        const val TEST_DATA_SOURCE = true
    }
}