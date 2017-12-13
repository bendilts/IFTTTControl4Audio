package com.bendilts.iftttcontrol4audiobridge.audio.control4;

import com.bendilts.iftttcontrol4audiobridge.audio.control4.Control4Device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Control4Radio extends Control4Device {
    public static final String[] stations = new String[] {
            "89.1",
            "90.1",
            "94.9",
            "97.1",
            "99.5",
            "100.3",
    };

    public Control4Radio() {
        super("192.168.1.49");
    }

    public Map<Integer,String> currentStations = new HashMap();

    public void tune(int tuner, String station) {
        currentStations.put(tuner, station);
        String hexStation = String.format("%x", (int)(Float.parseFloat(station)*10));
        String tunerName = tuner == 1 ? "a" : "b";

        //The radio sometimes needs to be convinced to change stations.
        for(int i = 0; i < 5; i++) {
            sendToDevice(String.format("c4.mt.t%sfreq 00 %s", tunerName, hexStation));
        }
    }
}
