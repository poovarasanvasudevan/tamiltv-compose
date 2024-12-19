package com.poovarasan.tamiltv.widget

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.poovarasan.tamiltv.R

@Composable
fun AppBanner(modifier: Modifier = Modifier.fillMaxWidth()) {
    val context = LocalContext.current as Activity
    val isAdFailed = remember { mutableStateOf(false) }
    val bannerWrapper = TMBannerWrapper(context)
    val bannerImage = painterResource(id = R.drawable.banner)

    val onLoaded = object: AppAdListener {
        override fun onAdLoaded() {
        }

        override fun onAdFailedToLoad() {
            isAdFailed.value = true
        }
    }

    if(isAdFailed.value) {
        Image(painter = bannerImage, contentDescription = "defaultBanner", modifier= Modifier.fillMaxWidth().height(50.dp).clickable {
            val uri: Uri = Uri.parse("https://play.google.com/store/apps/developer?id=Poovarasan+Vasudevan")
            val goMarket = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(goMarket)
        })
    } else {
        AndroidView(
            factory = { bannerWrapper.apply { loadAd(context, onLoaded) } },
            modifier = modifier.background(colorResource(R.color.bgcolor))
        )
    }
}


@Composable
fun AppBannerMRec(modifier: Modifier = Modifier.fillMaxWidth()) {
    val context = LocalContext.current as Activity
    val bannerWrapper = TMMRecBannerWrapper(context)

    AndroidView(
        factory = { bannerWrapper.apply {  loadAd(context)  } },
        modifier = modifier.background(colorResource(R.color.bgcolor))
    )
}

@Composable
fun StartAppAppBanner(modifier: Modifier = Modifier.fillMaxWidth(), onError : () -> Unit) {
    val context = LocalContext.current as Activity
    val bannerWrapper = StartAppMrecWrapper(context)

    AndroidView(
        factory = {
            bannerWrapper.apply {
                loadAd(context, object : AppAdListener {
                    override fun onAdLoaded() {
                    }

                    override fun onAdFailedToLoad() {
                        onError()
                    }
                })
            }
        },
        modifier = modifier.background(colorResource(R.color.bgcolor))
    )
}