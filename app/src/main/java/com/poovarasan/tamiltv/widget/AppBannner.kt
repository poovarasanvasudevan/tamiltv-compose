package com.poovarasan.tamiltv.widget

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.viewinterop.AndroidView
import com.poovarasan.tamiltv.R

@Composable
fun AppBanner(modifier: Modifier = Modifier.fillMaxWidth()) {
    val context = LocalContext.current as Activity
    val bannerWrapper = TMBannerWrapper(context)

    AndroidView(
        factory = {
            bannerWrapper.apply {
                loadAd(context)
            }
        },
        modifier = modifier.background(colorResource(R.color.bgcolor))
    )
}