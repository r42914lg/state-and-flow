package com.r42914lg.tryflow

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        const val TEST_DATA_SOURCE = true
    }
}