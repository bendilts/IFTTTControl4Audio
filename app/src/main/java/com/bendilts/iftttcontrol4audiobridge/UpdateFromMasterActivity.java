package com.bendilts.iftttcontrol4audiobridge;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;

public class UpdateFromMasterActivity extends Activity {
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
        resetUfmTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        resetUfmTimer();
        CommandExecutor.getInstance().updateFromMaster();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopUfmTimer();
    }
}
