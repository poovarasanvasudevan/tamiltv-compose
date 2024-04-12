package com.poovarasan.tamiltv.core

import android.app.PendingIntent
import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.extractor.DefaultExtractorsFactory
import androidx.media3.ui.PlayerNotificationManager
import com.koushikdutta.ion.Ion
import com.poovarasan.tamiltv.R

enum class PlayerState {
    BUFFERING, PLAYING, PAUSED, STOPPED, ERROR, IDLE, NONE
}


class AudioVM : ViewModel() {

    private val _currentFM = MutableLiveData(listOf<Radio>())
    val currentFM: LiveData<List<Radio>> = _currentFM


    private val _currentPlaying = MutableLiveData(Radio("", "", ""))
    val currentPlaying: LiveData<Radio> = _currentPlaying

    private val _playerState = MutableLiveData(PlayerState.STOPPED)
    val playerState: LiveData<PlayerState> = _playerState

    @UnstableApi
    fun setCurrent(fm: List<Radio>, index: Int = 0) {
        _currentFM.value = fm
        play(index)
    }

    @OptIn(UnstableApi::class)
    private fun constructMediaDescription(): PlayerNotificationManager.MediaDescriptionAdapter {
        return object : PlayerNotificationManager.MediaDescriptionAdapter {

            override fun getCurrentSubText(player: Player): String? {
                val md = player.currentMediaItem?.playbackProperties?.tag as Radio
                return md.name ?: ""
            }

            override fun getCurrentContentTitle(player: Player): String {
                val md = player.currentMediaItem?.playbackProperties?.tag as Radio
                return md.name ?: ""
            }

            override fun createCurrentContentIntent(player: Player): PendingIntent? {
                return null
            }

            override fun getCurrentContentText(player: Player): String? {
                return "Tamil Local Cable TV & Radio"
            }

            override fun getCurrentLargeIcon(
                player: Player,
                callback: PlayerNotificationManager.BitmapCallback
            ): Bitmap? {
                val md = player.currentMediaItem?.playbackProperties?.tag as Radio
                Ion.with(TamilTV.instance)
                    .load(md.logo)
                    .asBitmap()
                    .setCallback { e, result ->
                        if (e == null) {
                            callback.onBitmap(result)
                        }
                    }

                return null;
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun setNotfication(player: Player, metaData: Radio) {
        val playerNotificationManager = PlayerNotificationManager.Builder(
            TamilTV.instance,
            1123,
            "My_channel_id"
        )
            .setChannelDescriptionResourceId(R.string.channelDescription)
            .setChannelNameResourceId(R.string.channelName)
            .setMediaDescriptionAdapter(constructMediaDescription())
            .build()


        playerNotificationManager.setColorized(true)
        playerNotificationManager.setUseChronometer(true)
        playerNotificationManager.setPlayer(player)
    }

    @OptIn(UnstableApi::class)
    private fun constructPlayList(): List<MediaSource> {
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)

        return _currentFM.value!!.map { x ->
            val fac = DefaultMediaSourceFactory(dataSourceFactory, DefaultExtractorsFactory())
            fac.createMediaSource(MediaItem.Builder().setUri(x.url).setTag(x).build())
        }

    }

    fun playIndex(index: Int) {
        val player = TamilTV.player
        player.prepare()
        player.play()

        _currentPlaying.value = _currentFM.value?.get(index)
    }

    @UnstableApi
    fun play(index: Int = 0, listener: Player.Listener? = null) {
        val player = TamilTV.player
        if (player.isPlaying) {
            player.stop()
        }
        player.setMediaSources(constructPlayList())
        setNotfication(player, _currentFM.value!!.get(index))
        player.repeatMode = Player.REPEAT_MODE_ALL
        player.prepare()
        if (listener != null) {
            player.addListener(listener)
        }
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playerState.value = if (isPlaying) PlayerState.PLAYING else PlayerState.PAUSED
            }

            override fun onPlayerError(error: PlaybackException) {
                _playerState.value = PlayerState.ERROR
            }

            override fun onPlaybackStateChanged(state: Int) =
                when (state) {
                    Player.STATE_IDLE -> _playerState.value = PlayerState.IDLE
                    Player.STATE_BUFFERING -> _playerState.value = PlayerState.BUFFERING
                    Player.STATE_ENDED -> _playerState.value = PlayerState.STOPPED
                    else -> _playerState.value = PlayerState.NONE
                }

        })
        player.play()

        _currentPlaying.value = _currentFM.value?.get(index)

    }

    fun pause() {
        val player = TamilTV.player
        player.pause()
    }

    fun stop() {
        val player = TamilTV.player
        player.stop()
    }

    fun togglePlayPause() {
        val player = TamilTV.player
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }
}