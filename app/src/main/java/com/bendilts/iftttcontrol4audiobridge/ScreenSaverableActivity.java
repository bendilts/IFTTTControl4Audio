package com.bendilts.iftttcontrol4audiobridge;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

public class ScreenSaverableActivity extends Activity {
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


    //2 seconds after interaction (and on activating the activity), update audio system from master
    public static final long UPDATE_FROM_MASTER_TIMEOUT = 2 * 1000;

    private Handler ufmHandler = new Handler(){
        public void handleMessage(Message msg) {}
    };

    private Runnable ufmCallback = new Runnable() {
        @Override
        public void run() {
            CommandExecutor.getInstance().updateFromMaster();
        }
    };

    public void resetUfmTimer(){
        ufmHandler.removeCallbacks(ufmCallback);
        ufmHandler.postDelayed(ufmCallback, UPDATE_FROM_MASTER_TIMEOUT);
    }

    public void stopUfmTimer(){
        ufmHandler.removeCallbacks(ufmCallback);
    }

    @Override
    public void onUserInteraction(){
        resetScreenSaverTimer();
        resetUfmTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        resetScreenSaverTimer();
        resetUfmTimer();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        CommandExecutor.getInstance().updateFromMaster();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopScreenSaverTimer();
        stopUfmTimer();
    }
}
