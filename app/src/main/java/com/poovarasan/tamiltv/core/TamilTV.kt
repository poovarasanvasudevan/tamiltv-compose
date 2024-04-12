package com.poovarasan.tamiltv.core

import android.app.Application
import android.content.ContextWrapper
import androidx.media3.exoplayer.ExoPlayer
import com.facebook.ads.AudienceNetworkAds
import com.pixplicity.easyprefs.library.Prefs
import com.poovarasan.tamiltv.Database
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import com.startapp.sdk.adsbase.StartAppSDK

class TamilTV: Application() {

    companion object{
        lateinit var instance : TamilTV
        lateinit var player: ExoPlayer
        lateinit var driver :SqlDriver
        lateinit var database :Database
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        player = ExoPlayer.Builder(this)
            .build()

        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()

        driver = AndroidSqliteDriver(Database.Schema, applicationContext, "tamiltv.db")
        database = Database(driver)

        AudienceNetworkAds.initialize(this)
        StartAppSDK.setTestAdsEnabled(false)
    }
}