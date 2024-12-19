package com.poovarasan.tamiltv.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.poovarasan.tamiltv.R;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.ads.banner.BannerListener;
import com.startapp.sdk.ads.banner.Mrec;

public class StartAppMrecWrapper extends FrameLayout {

    Mrec banner;

    public StartAppMrecWrapper(@NonNull Context context) {
        super(context);
        init();
    }

    public StartAppMrecWrapper(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StartAppMrecWrapper(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public StartAppMrecWrapper(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void loadAd(Context context, AppAdListener appAdListener) {
        banner.loadAd();

        banner.setBannerListener(new BannerListener() {
            @Override
            public void onReceiveAd(View view) {
                banner.showBanner();
                appAdListener.onAdLoaded();            }

            @Override
            public void onFailedToReceiveAd(View view) {
                banner.hideBanner();
                appAdListener.onAdFailedToLoad();
            }

            @Override
            public void onImpression(View view) {

            }

            @Override
            public void onClick(View view) {

            }
        });
    }

    public void init() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        layoutInflater.inflate(R.layout.start_app_mrec, this);
        banner = findViewById(R.id.mrec);
    }
}
