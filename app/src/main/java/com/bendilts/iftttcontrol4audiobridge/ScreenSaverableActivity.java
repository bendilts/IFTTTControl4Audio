package com.bendilts.iftttcontrol4audiobridge;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;

public class ScreenSaverableActivity extends Activity {
    public static final long DISCONNECT_TIMEOUT = 60 * 1000;

    private Handler disconnectHandler = new Handler(){
        public void handleMessage(Message msg) {}
    };

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(ScreenSaverableActivity.this, ScreenSaverActivity.class);
            startActivity(intent);
        }
    };

    public void resetDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction(){
        resetDisconnectTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        resetDisconnectTimer();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }

}
