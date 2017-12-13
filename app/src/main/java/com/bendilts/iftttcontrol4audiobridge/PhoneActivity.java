package com.bendilts.iftttcontrol4audiobridge;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.widget.GridView;

import com.bendilts.iftttcontrol4audiobridge.audio.AudioSystem;
import com.bendilts.iftttcontrol4audiobridge.audio.control4.Control4Device;
import com.bendilts.iftttcontrol4audiobridge.audio.output.OutputDevice;

import static android.content.ContentValues.TAG;

public class PhoneActivity extends UpdateFromMasterActivity implements OutputDevice.DeviceListener {

    AudioSystem system;
    GridView mainGrid;
    MainInputGridAdapter mainGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        system = AudioSystem.getInstance(this);
        for(OutputDevice device : system.outputDevices) {
            device.listeners.add(this);
        }

        mainGrid = (GridView)findViewById(R.id.mainInputGrid);
        mainGridAdapter = new MainInputGridAdapter(this, PhoneInputActivity.class, true);
        mainGrid.setAdapter(mainGridAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateControls();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for(OutputDevice device : system.outputDevices) {
            device.listeners.remove(this);
        }
    }

    private void updateControls() {
        mainGridAdapter.notifyDataSetChanged();

        switch(getScreenOrientation()) {
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                mainGrid.setNumColumns(2);
                break;
            default:
                mainGrid.setNumColumns(3);
                break;
        }
    }

    @Override
    public void onDeviceChange() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateControls();
            }
        });
    }

    private int getScreenOrientation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch(rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    Log.e(TAG, "Unknown screen orientation. Defaulting to " +
                            "portrait.");
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch(rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    Log.e(TAG, "Unknown screen orientation. Defaulting to " +
                            "landscape.");
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }
}
