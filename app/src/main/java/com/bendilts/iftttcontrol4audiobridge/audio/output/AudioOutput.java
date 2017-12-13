package com.bendilts.iftttcontrol4audiobridge.audio.output;

import com.bendilts.iftttcontrol4audiobridge.audio.input.AudioInput;

// Represents an output, such as a pair of speakers, that is provided by an OutputDevice.
public class AudioOutput {
    public OutputDevice device;

    public int id; //Unique app-wide, used in network protocols
    public String name; //Displayed in the UI
    public int gain; //Volume gain relative to other outputs

    public int currentVolume = 0;
    public AudioInput currentInput = null;

    AudioOutput(OutputDevice d, int id, String n, int g) {
        device = d;
        this.id = id;
        name = n;
        gain = g;
    }

}
