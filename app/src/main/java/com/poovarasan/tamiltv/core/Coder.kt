package com.poovarasan.tamiltv.core

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion
import com.poovarasan.tamiltv.Ads
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun Context.getCoder() = suspendCoroutine<JsonArray> {

    Ion.with(this)
        .load(Ads.url)
        .asJsonArray()
        .setCallback { e, result ->
            if (e == null) it.resume(result)
            else it.resumeWithException(e)
        }
}

suspend fun Context.checkAPI(code: String) = suspendCoroutine<JsonArray> {
    Ion.with(this)
        .load("${Ads.murl}/${code}.json")
        .asJsonArray()
        .setCallback { e, result ->
            if (e == null) it.resume(result)
            else it.resumeWithException(e)
        }
}

suspend fun Context.getConfig() = suspendCoroutine<JsonObject> {
    Ion.with(this)
        .load(Ads.aaUrl)
        .noCache()
        .asJsonObject()
        .setCallback { e, result ->
            if (e == null) it.resume(result)
            else it.resumeWithException(e)
        }
}

suspend fun Context.getRadio() = suspendCoroutine<JsonObject> {
    Ion.with(this)
        .load(Ads.RUrl)
        .asJsonObject()
        .setCallback { e, result ->
            if (e == null) it.resume(result)
            else it.resumeWithException(e)
        }
}

data class Radio(
    val url: String?,
    val logo: String?,
    val name: String?
)


data class AppChannel(
    val channelId: Long?,
    val category: String?,
    val logo: String?,
    val name: String?,
    val url: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(channelId!!)
        parcel.writeString(category)
        parcel.writeString(logo)
        parcel.writeString(name)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AppChannel> {
        override fun createFromParcel(parcel: Parcel): AppChannel {
            return AppChannel(parcel)
        }

        override fun newArray(size: Int): Array<AppChannel?> {
            return arrayOfNulls(size)
        }
    }
}

//
fun Context.isOnline(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val n = cm.activeNetwork
        if (n != null) {
            val nc = cm.getNetworkCapabilities(n)
            return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI
            )
        }
        return false
    } else {
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}

object VideoPlayerConfig {
    //Minimum Video you want to buffer while Playing
    const val MIN_BUFFER_DURATION = 2000

    //Max Video you want to buffer during PlayBack
    const val MAX_BUFFER_DURATION = 5000

    //Min Video you want to buffer before start Playing it
    const val MIN_PLAYBACK_START_BUFFER = 1500

    //Min video You want to buffer when user resumes video
    const val MIN_PLAYBACK_RESUME_BUFFER = 2000
}

fun Context.help() {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data =
        Uri.parse("mailto:${Ads.EMAIL}?subject=${Ads.SUBJECT}") // only email apps should handle this
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    }
}