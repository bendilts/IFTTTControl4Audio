package com.bendilts.iftttcontrol4audiobridge;

public class AudioZone {
    public String name;
    public AudioOutput[] outputs;

    public AudioZone(String n, AudioOutput[] o) {
        name = n;
        outputs = o;
    }
}
