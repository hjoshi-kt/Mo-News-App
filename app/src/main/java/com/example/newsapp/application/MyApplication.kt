package com.example.newsapp.application

import android.app.Application
import com.example.newsapp.R
import com.example.newsapp.util.Utils
import com.moengage.core.DataCenter
import com.moengage.core.LogLevel
import com.moengage.core.MoEngage
import com.moengage.core.analytics.MoEAnalyticsHelper
import com.moengage.core.config.FcmConfig
import com.moengage.core.config.LogConfig
import com.moengage.core.config.NotificationConfig
import com.moengage.core.enableAdIdTracking
import com.moengage.core.model.UserGender
import java.util.Date

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val moEngage = MoEngage.Builder(this, Utils.MOENGAGE_APP_ID, DataCenter.DATA_CENTER_1)
            .configureLogs(LogConfig(LogLevel.VERBOSE, true))
            .configureNotificationMetaData(NotificationConfig(R.drawable.location, R.drawable.location))
            .build()
        MoEngage.initialiseDefaultInstance(moEngage)
        MoEAnalyticsHelper.setUniqueId(this, "mo-joshi")
        MoEAnalyticsHelper.setFirstName(this,"Himanshu")
        MoEAnalyticsHelper.setLastName(this,"Joshi")
        MoEAnalyticsHelper.setUserName(this,"Himanshu Joshi")
        MoEAnalyticsHelper.setGender(this, UserGender.MALE)
        MoEAnalyticsHelper.setMobileNumber(this,"8394023254")
        MoEAnalyticsHelper.setBirthDate(this, Date(2024,0,21))
        MoEAnalyticsHelper.setEmailId(this,"himanshu.joshi@moengage.com")
        MoEAnalyticsHelper.setUserAttribute(this,"locality", "Uttarakhand")
        enableAdIdTracking(this)
    }
}