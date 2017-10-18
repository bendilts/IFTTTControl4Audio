package com.bendilts.iftttcontrol4audiobridge;

import android.view.LayoutInflater;
import android.view.View;

public class AudioInput {
    public int index;
    public String name;
    public String searchName;
    public int gain;
    public int iconResource;

    AudioInput(int i, String n, String s, int g, int res) {
        index = i;
        name = n;
        gain = g;
        searchName = s;
        iconResource = res;
    }

    public View getInputControls(LayoutInflater inflater) {
        return null;
    }
}
