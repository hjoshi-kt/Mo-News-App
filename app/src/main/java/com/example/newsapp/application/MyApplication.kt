package com.example.newsapp.application

import android.app.Application
import com.example.newsapp.util.Utils
import com.moengage.core.DataCenter
import com.moengage.core.MoEngage
import com.moengage.core.config.FcmConfig
import com.moengage.core.enableAdIdTracking

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val moEngage = MoEngage.Builder(this, Utils.MOENGAGE_APP_ID, DataCenter.DATA_CENTER_1)
            .build()
        MoEngage.initialiseDefaultInstance(moEngage)
        enableAdIdTracking(this)
    }
}