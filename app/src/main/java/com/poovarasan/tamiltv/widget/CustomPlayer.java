package com.poovarasan.tamiltv.widget;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.datasource.rtmp.RtmpDataSource;
import androidx.media3.datasource.rtmp.RtmpDataSourceFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.dash.DashMediaSource;
import androidx.media3.exoplayer.dash.DefaultDashChunkSource;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.exoplayer.smoothstreaming.DefaultSsChunkSource;
import androidx.media3.exoplayer.smoothstreaming.SsMediaSource;
import androidx.media3.exoplayer.source.BaseMediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.ui.AspectRatioFrameLayout;
import androidx.media3.ui.PlayerView;

import com.google.android.material.button.MaterialButton;
import com.poovarasan.tamiltv.R;

public class CustomPlayer extends FrameLayout {
    PlayerView playerView;
    String url;
    ExoPlayer player;
    MaterialButton fullScreen;
    MaterialButton fitPicture;


    public CustomPlayer(@NonNull Context context) {
        super(context);
        init((Activity) context);
    }

    public CustomPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init((Activity) context);
    }

    public CustomPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init((Activity) context);
    }

    public CustomPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init((Activity) context);
    }


    void init(Activity activity) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        layoutInflater.inflate(R.layout.player_main, this);

        playerView = findViewById(R.id.mPlayerView);
        fullScreen = findViewById(R.id.fullScreenBtn);
        fitPicture = findViewById(R.id.fitPicture);

        fitPicture.setOnClickListener(new OnClickListener() {
            @Override
            @UnstableApi
            public void onClick(View v) {
                if (playerView.getResizeMode() == AspectRatioFrameLayout.RESIZE_MODE_ZOOM) {
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                    fitPicture.setText("FILL");
                } else if (playerView.getResizeMode() == AspectRatioFrameLayout.RESIZE_MODE_FIT) {
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                    fitPicture.setText("ZOOM");
                } else {
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                    fitPicture.setText("FIT");
                }

            }
        });
    }

    public void setPlayer(ExoPlayer player) {
        this.player = player;
        playerView.setPlayer(player);
    }

    @OptIn(markerClass = UnstableApi.class)
    public void setResizeMode(Integer playerResolution) {
        playerView.setResizeMode(playerResolution);
    }

    public void setOnFullScreenClick(OnClickListener click) {
        fullScreen.setOnClickListener(click);
    }


    public void setUrl(String url) {
        this.url = url;
    }

    @UnstableApi
    public BaseMediaSource buildMediaSource(Uri uri, String str) {
        int i;
        if (TextUtils.isEmpty(str)) {
            i = Util.inferContentType(uri);
        } else {
            i = Util.inferContentType(".$str");
        }

        Log.i("AAction", Integer.toString(i));

        if (i == C.CONTENT_TYPE_DASH) {
            Log.i("AAction", "Dash");
            return new DashMediaSource.Factory(
                    new DefaultDashChunkSource.Factory(buildDataSourceFactory()),
                    buildDataSourceFactory()
            ).createMediaSource(MediaItem.fromUri(uri));
        } else if (i == C.CONTENT_TYPE_SS) {
            Log.i("AAction", "SS");
            return new SsMediaSource.Factory(
                    new DefaultSsChunkSource.Factory(buildDataSourceFactory()),
                    buildDataSourceFactory()
            ).createMediaSource(MediaItem.fromUri(uri));
        } else if (i == C.CONTENT_TYPE_HLS) {
            Log.i("AAction", "HLS");
            return new HlsMediaSource.Factory(buildDataSourceFactory())
                    .createMediaSource(MediaItem.fromUri(uri));
        } else if (i == C.CONTENT_TYPE_RTSP) {
            Log.i("AAction", "RTMP");
            RtmpDataSourceFactory fac = new RtmpDataSourceFactory();
            return new ProgressiveMediaSource.Factory(fac).createMediaSource(MediaItem.fromUri(uri));
        } else {
            Log.i("AAction", "PG");
            if (uri.toString().startsWith("http")) {
                return new HlsMediaSource.Factory(buildDataSourceFactory())
                        .createMediaSource(MediaItem.fromUri(uri));
            }
            if (uri.toString().startsWith("rtmp")) {
                RtmpDataSource.Factory fac = new RtmpDataSource.Factory();
                return new ProgressiveMediaSource.Factory(fac).createMediaSource(MediaItem.fromUri(uri));
            }

            return new ProgressiveMediaSource
                    .Factory(buildDataSourceFactory())
                    .createMediaSource(MediaItem.fromUri(uri));
        }
    }


    @UnstableApi
    private DataSource.Factory buildDataSourceFactory() {
        return new DefaultHttpDataSource.Factory()
                .setAllowCrossProtocolRedirects(true)
                .setUserAgent(Util.getUserAgent(getContext(), "TamilTV"));
    }

    @UnstableApi public void play() {
        player.setMediaSource(buildMediaSource(Uri.parse(this.url), ""));
        player.setPlayWhenReady(true);
    }

    void pause() {
        player.pause();
    }
}
