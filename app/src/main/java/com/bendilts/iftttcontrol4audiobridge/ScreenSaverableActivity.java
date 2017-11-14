package com.bendilts.iftttcontrol4audiobridge;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

public class ScreenSaverableActivity extends UpdateFromMasterActivity {
    public static final long SCREENSAVER_TIMEOUT = 60 * 1000;

    private Handler screenSaverHandler = new Handler(){
        public void handleMessage(Message msg) {}
    };

    private Runnable screenSaverCallback = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(ScreenSaverableActivity.this, ScreenSaverActivity.class);
            startActivity(intent);
        }
    };

    public void resetScreenSaverTimer(){
        screenSaverHandler.removeCallbacks(screenSaverCallback);
        screenSaverHandler.postDelayed(screenSaverCallback, SCREENSAVER_TIMEOUT);
    }

    public void stopScreenSaverTimer(){
        screenSaverHandler.removeCallbacks(screenSaverCallback);
    }


    @Override
    public void onUserInteraction(){
        super.onUserInteraction();
        resetScreenSaverTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        resetScreenSaverTimer();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onStop() {
        super.onStop();
        stopScreenSaverTimer();
    }
}
