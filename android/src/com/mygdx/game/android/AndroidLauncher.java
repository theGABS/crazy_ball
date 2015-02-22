package com.mygdx.game.android;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import com.google.android.gms.ads.AdListener;
import com.mygdx.game.IActivityRequestHandler;
import com.mygdx.game.MyGame;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.InterstitialAd;


public class AndroidLauncher extends AndroidApplication implements IActivityRequestHandler {
    InterstitialAd interstitial;
    AdView adView;
    AdRequest.Builder adRequestBuilder;
    private final int SHOW_ADS = 1;
    private final int LOAD_ADS = 0;

    protected Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case SHOW_ADS:
                {
                    interstitial.show();
                    break;
                }
                case LOAD_ADS:
                {
                    interstitial = new InterstitialAd(getApplicationContext());
                    interstitial.setAdUnitId("ca-app-pub-6798653878803807/3584243573");

                    adRequestBuilder = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR);


                    interstitial.loadAd(adRequestBuilder.build());
                    interstitial.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
                            interstitial.loadAd(adRequest);
                        }
                    });
                    break;
                }
            }
        }
    };


	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        View gameView = initializeForView(new MyGame(this), config);




        layout.addView(gameView);
        setContentView(layout);

	}

    @Override
    public void showAds(boolean show) {
        handler.sendEmptyMessage(show ? SHOW_ADS : LOAD_ADS);
    }
}
