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

public class TMMRecBannerWrapper extends FrameLayout {

    FrameLayout bannerLayout;
    MaxAdView adView;

    public TMMRecBannerWrapper(@NonNull Context context) {
        super(context);
        init(context);
    }

    public TMMRecBannerWrapper(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TMMRecBannerWrapper(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public TMMRecBannerWrapper(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    public void init(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        layoutInflater.inflate(R.layout.td_banner, this);
        bannerLayout = findViewById(R.id.tmBanner);
    }

    public void loadAd(Activity activity) {
        adView = new MaxAdView( "669e19d727d7f4ae", MaxAdFormat.MREC, activity );
        adView.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(MaxAd ad) {
                Log.i("MREC_ADERROR","EXPAND");
            }

            @Override
            public void onAdCollapsed(MaxAd ad) {

            }

            @Override
            public void onAdLoaded(MaxAd ad) {
                Log.i("MREC_ADERROR","LOADED");
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
                Log.i("MREC_ADERROR","DISPLAYED");
            }

            @Override
            public void onAdHidden(MaxAd ad) {
                Log.i("MREC_ADERROR","HIDE");
            }

            @Override
            public void onAdClicked(MaxAd ad) {
                Log.i("MREC_ADERROR","CLICK");
            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                Log.i("MREC_ADERROR",error.getMessage());
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                Log.i("MREC_ADERROR",error.getMessage());
            }
        });

        int width = JavaUtils.dpToPx( 300, activity );
        int heightPx = JavaUtils.dpToPx( 250, activity );
        adView.setLayoutParams( new LayoutParams( width, heightPx ) );
        adView.setBackgroundColor(ContextCompat.getColor(activity,android.R.color.transparent));

        bannerLayout.addView(adView);
        adView.loadAd();
    }

    public void destroyBanner() {
    }
}
