package com.r42914lg.tryflow.utils

import android.util.Log
import com.r42914lg.tryflow.BuildConfig

inline fun <reified T> T.log(message: String) {
    if (BuildConfig.DEBUG)
        Log.d("LG>" + T::class.java.simpleName, message)
}