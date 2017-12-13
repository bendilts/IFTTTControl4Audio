package com.bendilts.iftttcontrol4audiobridge.audio.input;

import android.view.LayoutInflater;
import android.view.View;

import com.bendilts.iftttcontrol4audiobridge.audio.output.AudioOutput;

import java.util.ArrayList;
import java.util.List;

// Represents one source of audio, such as a radio or Chromecast Audio or a microphone. Each input
// can be supported by one or more OutputDevices, which in turn aggregate one or more AudioOutputs
// that these AudioInputs can be routed to.
//
// For example, the Chromecast Audio input could be available as an input to multiple receivers (by
// having multiple CCA devices in an output group), each of which power one or more pairs of
// speakers as AudioOutputs.
public class AudioInput {
    public int id; //Unique app-wide, used in network protocols

    public String name; //Displayed in the UI
    public String searchName; //For voice commands, the shortest unique string to identify this
    public int gain; //Volume gain relative to other AudioInputs
    public int iconResource; //The icon to use on the home screen and elsewhere to identify this

    public List<AudioOutput> availableOuputs = new ArrayList();

    public AudioInput(int id, String n, String s, int g, int res) {
        this.id = id;
        name = n;
        gain = g;
        searchName = s;
        iconResource = res;
    }

    public View getInputControls(LayoutInflater inflater, int textSize) {
        return null;
    }
}
