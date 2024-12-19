package com.poovarasan.tamiltv.core

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration
import com.applovin.sdk.AppLovinSdkInitializationConfiguration
import com.pixplicity.easyprefs.library.Prefs
import com.startapp.sdk.adsbase.Ad
import com.startapp.sdk.adsbase.StartAppAd
import com.startapp.sdk.adsbase.adlisteners.AdEventListener



fun Activity.initAd(onSuccess: () -> Unit) {
    val initConfig = AppLovinSdkInitializationConfiguration
        .builder("VtcAm_8lgPQSl52iwTS6iWxXiOR8VkQ6Dbz4LlTpTb6P6Se1ZEIGqkzKRkW_W6X64G8nQxAtCdpJUr3uy4Syew", this)
        .setMediationProvider(AppLovinMediationProvider.MAX)
        .build()
    AppLovinSdk.getInstance(this).initialize(initConfig) { _config: AppLovinSdkConfiguration ->
        onSuccess()
    }
}

fun Activity.showStartAppIntAds(onFinished: () -> Unit) {
    val startApp = StartAppAd(this)
    startApp.loadAd(StartAppAd.AdMode.AUTOMATIC, object : AdEventListener {
        override fun onReceiveAd(p0: Ad) {
            startApp.showAd()
            onFinished()
        }

        override fun onFailedToReceiveAd(p0: Ad?) {
            onFinished()
        }
    })

}

fun Activity.showIntAd(onFinished: () -> Unit) {
    val activity = this
    val inter = Prefs.getInt("intcount", 0)
    val tcount = Prefs.getInt("tcount", 10)

    if (inter > 0 && inter % tcount == 0) {
        val interstitialAd = MaxInterstitialAd("1f60a970ce0d6d2d", this)
        interstitialAd.setListener(
          object: MaxAdListener {
              override fun onAdLoaded(p0: MaxAd) {
                  interstitialAd.showAd();
              }

              override fun onAdDisplayed(p0: MaxAd) {
                  TODO("Not yet implemented")
              }

              override fun onAdHidden(p0: MaxAd) {
                  Prefs.putInt("intcount", inter + 1)
                  onFinished()
              }

              override fun onAdClicked(p0: MaxAd) {
                  Prefs.putInt("intcount", inter + 1)
                  onFinished()
              }

              override fun onAdLoadFailed(p0: String, p1: MaxError) {
                  activity.showStartAppIntAds(onFinished)
              }

              override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                  activity.showStartAppIntAds(onFinished)
              }

          }
        );
        interstitialAd.loadAd()
    } else {
        Prefs.putInt("intcount", inter + 1)
        onFinished()
    }
}


fun Context.openPlayStore() {
    try {
        val uri: Uri = Uri.parse("market://details?id=${packageName}")
        val goMarket = Intent(Intent.ACTION_VIEW, uri)
        startActivity(goMarket)
    } catch (e: ActivityNotFoundException) {
        val uri: Uri =
            Uri.parse("https://play.google.com/store/apps/details?id=${packageName}")
        val goMarket = Intent(Intent.ACTION_VIEW, uri)
        startActivity(goMarket)
    }
}