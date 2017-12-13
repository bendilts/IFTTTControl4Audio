package com.bendilts.iftttcontrol4audiobridge.audio.output;

import android.util.Log;

import com.bendilts.iftttcontrol4audiobridge.R;
import com.bendilts.iftttcontrol4audiobridge.audio.control4.Control4Device;
import com.bendilts.iftttcontrol4audiobridge.audio.input.AudioInput;
import com.bendilts.iftttcontrol4audiobridge.audio.input.RadioInput;
import com.bendilts.iftttcontrol4audiobridge.audio.output.AudioOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Control4Receiver extends OutputDevice {

    private Control4Device device = new Control4Device("192.168.1.39");

    public Control4Receiver() {
        outputs.put(6, new AudioOutput(this, 6, "Kitchen", 5));
        outputs.put(3, new AudioOutput(this, 3, "Office", 0));
        outputs.put(7, new AudioOutput(this, 7, "Library", 5));
        outputs.put(4, new AudioOutput(this, 4, "Front Room", 0));
        outputs.put(1, new AudioOutput(this, 1, "Master Bedroom", 0));
        outputs.put(2, new AudioOutput(this, 2, "Master Bathroom", 0));
        outputs.put(5, new AudioOutput(this, 5, "Deck", 10));
        outputs.put(8, new AudioOutput(this, 8, "Patio", 10));
    }

    public void sendChannelVol(AudioOutput output, int volume) {
        if(outputIndex(output) != -1) {
            super.sendChannelVol(output, volume);
            device.sendToDevice(String.format("c4.amp.chvol %02x %02x", outputIndex(output), finalVolume(output, volume) + 155));
            notifyListeners();
        }
    }

    public void sendChannelInput(AudioOutput output, AudioInput input) {
        if(outputIndex(output) != -1 && inputIndex(input) != -1) {
            super.sendChannelInput(output, input);
            device.sendToDevice(String.format("c4.amp.out %02x %02x", outputIndex(output), input == null ? 0 : inputIndex(input)));
            notifyListeners();
        }
    }

    public void sendChannelMute(AudioOutput output, boolean mute) {
        if(outputIndex(output) != -1) {
            super.sendChannelMute(output, mute);
            device.sendToDevice(String.format("c4.amp.mute %02x %02x", outputIndex(output), mute ? 1 : 0));
            notifyListeners();
        }
    }
}
