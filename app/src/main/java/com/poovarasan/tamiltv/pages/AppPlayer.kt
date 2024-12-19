package com.poovarasan.tamiltv.pages


import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.datasource.rtmp.RtmpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.dash.DefaultDashChunkSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.smoothstreaming.DefaultSsChunkSource
import androidx.media3.exoplayer.smoothstreaming.SsMediaSource
import androidx.media3.exoplayer.source.BaseMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.upstream.DefaultAllocator
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.navigation.NavController
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.poovarasan.tamiltv.R
import com.poovarasan.tamiltv.core.AppChannel
import com.poovarasan.tamiltv.core.TamilTV
import com.poovarasan.tamiltv.core.VideoPlayerConfig
import com.poovarasan.tamiltv.widget.AppBannerMRec
import com.poovarasan.tamiltv.widget.BackHandler
import com.poovarasan.tamiltv.widget.CustomPlayer
import com.poovarasan.tamiltv.widget.OnLifecycleEvent
import com.poovarasan.tamiltv.widget.UrlImage
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy


@OptIn(UnstableApi::class) @SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun AppPlayer(navController: NavController) {

    val channel = navController.previousBackStackEntry?.savedStateHandle?.get<AppChannel>("channel")
    val systemUI = rememberSystemUiController()
    val context = LocalContext.current as Activity
    val favQuery = TamilTV.database.favouriteQueries


    val isFavourite = favQuery.isFavourite(channel?.channelId)
        .asFlow()
        .mapToOne(Dispatchers.IO)
        .collectAsState(initial = 0L)

    val resizeModeFlow = AspectRatioFrameLayout.RESIZE_MODE_FIT.toString()


    val fullScreen = rememberSaveable { mutableStateOf(false) }
    val isError = rememberSaveable { mutableStateOf(false) }

    fun favClicked() {
        if (isFavourite.value > 0L) {
            favQuery.removeFavourite(channelId = channel?.channelId)
        } else {
            favQuery.addFavourite(channelId = channel?.channelId)
        }
    }

    fun buildDataSourceFactory(): DataSource.Factory {
        val cookieJar: ClearableCookieJar = PersistentCookieJar(
            SetCookieCache(),
            SharedPrefsCookiePersistor(context)
        )
        val okHttpClient: OkHttpClient = OkHttpClient.Builder().followRedirects(true).cookieJar(cookieJar).build()
        return OkHttpDataSource
            .Factory(okHttpClient)
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.80 Safari/537.36")
    }

    fun loadControl() : LoadControl {
        return DefaultLoadControl.Builder()
            .setAllocator(DefaultAllocator(true, 16))
            .setBufferDurationsMs(
                VideoPlayerConfig.MIN_BUFFER_DURATION,
                VideoPlayerConfig.MAX_BUFFER_DURATION,
                VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
                VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER
            )
            .setTargetBufferBytes(-1)
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()
    }

    fun buildMediaSource(uri: Uri, str: String): BaseMediaSource {

        if (uri.toString().endsWith("app=poov")) {
            return ProgressiveMediaSource
                .Factory(buildDataSourceFactory())
                .createMediaSource(MediaItem.fromUri(uri))
        }

        val i= Util.inferContentType(uri)
        if (i == C.CONTENT_TYPE_DASH) {
            Log.i("AAction", "Dash")
            return DashMediaSource.Factory(
                DefaultDashChunkSource.Factory(buildDataSourceFactory()),
                buildDataSourceFactory()
            ).createMediaSource(MediaItem.fromUri(uri))
        } else if (i == C.CONTENT_TYPE_SS) {
            Log.i("AAction", "SS")
            return SsMediaSource.Factory(
                DefaultSsChunkSource.Factory(buildDataSourceFactory()),
                buildDataSourceFactory()
            ).createMediaSource(MediaItem.fromUri(uri))
        } else if (i == C.CONTENT_TYPE_HLS) {
            Log.i("AAction", "HLS")
            return HlsMediaSource.Factory(buildDataSourceFactory())
                .createMediaSource(MediaItem.fromUri(uri))
        } else if (i == C.CONTENT_TYPE_RTSP) {
            Log.i("AAction", "RTMP")
            val fac = RtmpDataSource.Factory()
            return ProgressiveMediaSource.Factory(fac)
                .createMediaSource(MediaItem.fromUri(uri))
        } else {
            Log.i("AAction", "PG")
            if (uri.toString().startsWith("http")) {
                return HlsMediaSource.Factory(buildDataSourceFactory())
                    .createMediaSource(MediaItem.fromUri(uri))
            }
            if (uri.toString().startsWith("rtmp")) {
                val fac = RtmpDataSource.Factory()
                return ProgressiveMediaSource.Factory(fac).createMediaSource(MediaItem.fromUri(uri))
            }

            return ProgressiveMediaSource
                .Factory(buildDataSourceFactory())
                .createMediaSource(MediaItem.fromUri(uri))
        }
    }


    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(buildDataSourceFactory()))
            .setLoadControl(loadControl())
            .build().apply {
                setMediaSource(buildMediaSource(Uri.parse(channel?.url), ""))
                this.addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        error.printStackTrace()
                        isError.value = true
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        if (isPlaying) {
                            isError.value = false
                        }
                    }
                })
                prepare()
                playWhenReady = true
            }
    }


    fun makeFullScreen() {
        if (fullScreen.value) {
            context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            context.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            fullScreen.value = false
        } else {

            context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            context.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            fullScreen.value = true
        }
    }


    OnLifecycleEvent { e, v ->
        when (v) {
            Lifecycle.Event.ON_PAUSE -> {
                try {
                    exoPlayer.pause()
                } catch (e: Exception) {

                }
            }
            Lifecycle.Event.ON_DESTROY -> exoPlayer.release()
            Lifecycle.Event.ON_RESUME -> exoPlayer.play()
            else -> {
            }
        }
    }


    BackHandler {
        if (fullScreen.value) {
            makeFullScreen()
        } else {
            exoPlayer.release()
            navController.navigateUp()
        }
    }

    LaunchedEffect(Unit) {
        context.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        val cookieManager = CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(cookieManager)
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    )
    BoxWithConstraints {
        systemUI.setStatusBarColor(Color.Black, !isSystemInDarkTheme())
        systemUI.setNavigationBarColor(colorResource(R.color.appbarcolor), !isSystemInDarkTheme())

        val mm = Modifier.fillMaxWidth().focusable(true)
            .onKeyEvent { true }
            .background(color = Color.Black)
            .height(if (!fullScreen.value) 245.dp else maxHeight)

        AnimatedVisibility(visible = channel !=null) {
            Column {
                Box(modifier = mm, contentAlignment = Alignment.BottomCenter) {
                    AndroidView(
                        factory = {
                            CustomPlayer(context).apply {
                                setPlayer(exoPlayer)
                                setResizeMode(resizeModeFlow.toInt())
                                setOnFullScreenClick {
                                    makeFullScreen()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )


                    androidx.compose.animation.AnimatedVisibility(visible = isError.value) {
                        Box(modifier = Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.Center) {
                            Text(
                                "Oops there is error in Playing stream. Please try later",
                                style = MaterialTheme.typography.body1.copy(
                                    color = Color.White,
                                    fontSize = 16.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                if (!fullScreen.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colorResource(R.color.bgcolor))
                            .height(60.dp)
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .width(44.dp)
                                    .padding(2.dp)
                            ) {

                                UrlImage(
                                    url = channel?.logo ?: "",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(3.dp),
                                    contentScale = ContentScale.Crop,
                                    error = {
                                        Image(
                                            bitmap = ImageBitmap.imageResource(R.drawable.placeholder),
                                            ""
                                        )
                                    },
                                    loading = {
//                                    CircularProgressIndicator()
                                    },
                                )
                            }
                            Box(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = channel?.name ?: "",
                                    color = colorResource(R.color.textcolor),
                                    style = MaterialTheme.typography.body1.copy(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    text = channel?.category ?: "",
                                    color = colorResource(R.color.textcolor),
                                    style = MaterialTheme.typography.caption.copy(fontSize = 14.sp)
                                )
                            }

                            IconButton(onClick = { favClicked() }) {
                                Icon(
                                    if (isFavourite.value > 0L) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                                    "Fav",
                                    tint = Color.Blue
                                )
                            }
                        }
                    }

                    Box(modifier = Modifier.weight(1f).background(colorResource(R.color.bgcolor))) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize().padding(16.dp)
                        ) {
                           AppBannerMRec( modifier= Modifier )
                        }
                    }
                }
            }
        }
    }
}

