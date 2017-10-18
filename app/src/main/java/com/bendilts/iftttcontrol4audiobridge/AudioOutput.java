package com.bendilts.iftttcontrol4audiobridge;

public class AudioOutput {
    public int index;
    public String name;
    public int gain;

    public int currentVolume = 0;
    public AudioInput currentInput = null;

    AudioOutput(int i, String n, int g) {
        index = i;
        name = n;
        gain = g;
    }

}
