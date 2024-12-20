package com.poovarasan.tamiltv.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.poovarasan.tamiltv.R;

public class TMBannerWrapper extends FrameLayout {

    FrameLayout bannerLayout;
    MaxAdView adView;

    public TMBannerWrapper(@NonNull Context context) {
        super(context);
        init(context);
    }

    public TMBannerWrapper(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TMBannerWrapper(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public TMBannerWrapper(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    public void init(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        layoutInflater.inflate(R.layout.td_banner, this);
        bannerLayout = findViewById(R.id.tmBanner);
    }

    public void loadAd(Activity activity, AppAdListener appAdListener) {
        adView = new MaxAdView( "adb3502ae720aa3a", MaxAdFormat.BANNER, activity );
        adView.startAutoRefresh();
        adView.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(MaxAd ad) {
                Log.i("ADERROR","EXPAND");
            }

            @Override
            public void onAdCollapsed(MaxAd ad) {

            }

            @Override
            public void onAdLoaded(MaxAd ad) {
                Log.i("ADERROR","LOADED");
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
                appAdListener.onAdLoaded();
            }

            @Override
            public void onAdHidden(MaxAd ad) {
            }

            @Override
            public void onAdClicked(MaxAd ad) {
            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                appAdListener.onAdFailedToLoad();
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                appAdListener.onAdFailedToLoad();
            }
        });

        int widthPx = ViewGroup.LayoutParams.MATCH_PARENT;
        int heightPx = getResources().getDimensionPixelSize( R.dimen.banner_height );
        adView.setLayoutParams( new FrameLayout.LayoutParams( widthPx, heightPx ) );
        adView.setBackgroundColor(ContextCompat.getColor(activity,android.R.color.transparent));
        bannerLayout.addView(adView);
        adView.loadAd();
    }

    public void destroyBanner() {
    }
}
