package com.nousresearch.hermesagent

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HermesApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}
