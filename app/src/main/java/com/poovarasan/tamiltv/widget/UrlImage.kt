package com.poovarasan.tamiltv.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import com.koushikdutta.ion.Ion
import com.pixplicity.easyprefs.library.Prefs
import com.poovarasan.tamiltv.R


@Composable
fun UrlImage(
    url: String?,
    modifier: Modifier,
    contentScale: ContentScale = ContentScale.None,
    rotate: Float = 0f,
    error: @Composable (() -> Unit)? = null,
    loading: @Composable (() -> Unit?)? = null
) {
    val state = loadImage(url = url, rotate = rotate)
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,

        ) {
        when (state) {
            is RemoteImageState.Loading -> {
                if (loading != null) {
                    loading()
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
//                        CircularProgressIndicator(
//                            strokeWidth = 2.dp,
//                            modifier = Modifier.size(16.dp)
//                        )
                    }
                }
            }
            is RemoteImageState.Loaded -> {
                if(Prefs.getBoolean("showImage",true)) {
                    Image(
                        bitmap = state.image.asImageBitmap(),
                        "",
                        contentScale = contentScale,
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("loadedImg")
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.placeholder),
                        "",
                        contentScale = contentScale,
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("loadedImg")
                    )
                }
            }
            is RemoteImageState.LoadError -> {
                if (error != null) {
                    error()
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(text = "Could not load image")
                    }
                }
            }
        }
    }
}


@Composable
fun loadImage(
    url: String?,
    rotate: Float,
): RemoteImageState {
    var state by remember(url) {
        mutableStateOf<RemoteImageState>(RemoteImageState.Loading)
    }
    if (url.isNullOrEmpty()) {
        state = RemoteImageState.LoadError
    } else {

        Ion.with(LocalContext.current)
            .load(url)
            .asBitmap()
            .setCallback { e, result ->
                if (e == null) {
                    state = RemoteImageState.Loaded(result)
                } else {
                    state = RemoteImageState.LoadError
                }
            }
    }
    return state
}