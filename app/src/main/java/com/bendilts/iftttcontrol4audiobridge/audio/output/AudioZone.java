package com.bendilts.iftttcontrol4audiobridge.audio.output;

import com.bendilts.iftttcontrol4audiobridge.audio.output.AudioOutput;

public class AudioZone {
    public String name;
    public AudioOutput[] outputs;

    public AudioZone(String n, AudioOutput[] o) {
        name = n;
        outputs = o;
    }
}
