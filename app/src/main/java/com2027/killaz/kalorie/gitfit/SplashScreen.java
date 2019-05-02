package com2027.killaz.kalorie.gitfit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SplashScreen extends Activity {

    private Thread splashThread;
    private SharedPreferences prefs = null;

    /**
     * The splash screen of the app.
     */
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        startAnimation();
    }

    /**
     * An image is displayed using animations and the user is then sent to the login screen or to the OnBoarding introductory activity if its their first launch of the app.
     */
    private void startAnimation() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.splash_linear_layout);
        linearLayout.clearAnimation();
        linearLayout.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.splash_screen_img);
        iv.clearAnimation();
        iv.startAnimation(anim);

        splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (waited < 3500) {
                        sleep(100);
                        waited += 100;
                    }
                    if (prefs.getBoolean("firstRun", true)) {
                        prefs.edit().putBoolean("firstRun", false).apply();
                        Intent onBoarding = new Intent(SplashScreen.this, OnBoardingActivity.class);
                        onBoarding.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(onBoarding);
                        SplashScreen.this.finish();
                    } else {
                        Intent intent = new Intent(SplashScreen.this,
                                LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        SplashScreen.this.finish();
                    }

                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    SplashScreen.this.finish();
                }
            }
        };

        splashThread.start();
    }
}
