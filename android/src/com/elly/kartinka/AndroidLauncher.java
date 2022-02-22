package com.elly.kartinka;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Arrays;

public class AndroidLauncher extends AndroidApplication implements AdManager {

    private static final String BANNER_ID_TEST = "ca-app-pub-3940256099942544/6300978111";
    private static final String BANNER_ID = "ca-app-pub-5240552192099158/2461217351";
    private static final String INTERSTITIAL_ID = "ca-app-pub-5240552192099158/6111853600";

    private View gameView;
    private AdView adView;
    private RelativeLayout layout;
    private AdRequest adRequest;
    private GameClass game;

    private InterstitialAd interstitial;
    private InterstitialAdLoadCallback intCallback;
    private Runnable showAdR;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initGameAndBanner();

        initInterstitial();
    }

    private void initGameAndBanner() {
        //init
        final AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        layout = new RelativeLayout(this);
        adView = new AdView(this);
        game = new GameClass(this);
        gameView = initializeForView(game, config);
        adRequest = new AdRequest.Builder().build();

        //setup init features
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        //resizing
        adView.setAdSize(AdSize.FULL_BANNER);
        adView.setAdUnitId(BANNER_ID);
        adView.loadAd(adRequest);




        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                //parameters
                final RelativeLayout.LayoutParams gameParams =
                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                final RelativeLayout.LayoutParams adParams =
                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);


                int h = AdSize.FULL_BANNER.getHeight();
                RequestConfiguration requestConfiguration =
                        new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("emulator-5554", "realme-rmx2063-6ac8157f")).build();
                MobileAds.setRequestConfiguration(requestConfiguration);
                gameParams.bottomMargin = (int) (h * getResources().getDisplayMetrics().density);
                gameView.setLayoutParams(gameParams);
                layout.addView(adView, adParams);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                log("hello", loadAdError.getMessage());
            }
        });

        layout.addView(gameView);
        setContentView(layout);

        intCallback = new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                interstitial = interstitialAd;
                interstitial.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                        game.getMusic().pause();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        game.getMusic().play();
                        initInterstitial();
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                interstitial = null;
            }
        };

        final AndroidLauncher t = this;
        showAdR = new Runnable() {
            @Override
            public void run() {
                if (interstitial != null)
                    interstitial.show(t);
            }
        };
    }

    public void initInterstitial() {
        InterstitialAd.load(this, INTERSTITIAL_ID, adRequest, intCallback);
    }

    @Override
    public void showAd() {
        adView.post(showAdR);
    }
}
