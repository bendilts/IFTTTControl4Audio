package com.bendilts.iftttcontrol4audiobridge.audio.output;

import android.util.Log;

import com.bendilts.iftttcontrol4audiobridge.audio.input.AudioInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// One hardware device that controls one or more audio outputs, such as pairs of speakers. The
// inputs and outputs here are keyed NOT by their IDs, but instead by their output index on the
// device. Chromecast might be ID 4, but be at input index 2 on one receiver and input index 5
// on another.
public abstract class OutputDevice {
    public interface DeviceListener {
        void onDeviceChange();
    }

    public List<DeviceListener> listeners = new ArrayList();
    public void notifyListeners() {
        for(DeviceListener l : listeners) {
            l.onDeviceChange();
        }
    }

    public void sendChannelVol(AudioOutput output, int volume) {
        output.currentVolume = Math.max(0, Math.min(100, volume));
    }

    protected int finalVolume(AudioOutput output, int volume) {
        volume += output.gain + (output.currentInput == null ? 0 : output.currentInput.gain);
        volume = Math.max(1, Math.min(80, volume)); //Over 80 may damage my speakers
        return volume;
    }

    public void sendChannelInput(AudioOutput output, AudioInput input) {
        if(input != output.currentInput) {
            output.currentInput = input;

            if(output.currentVolume > 0) {
                sendChannelVol(output, output.currentVolume);
            }
        }
    }

    public void sendChannelMute(AudioOutput output, boolean mute) {
    }

    public Map<Integer, AudioInput> inputs = new HashMap();
    public Map<Integer, AudioOutput> outputs = new HashMap();

    public int outputIndex(AudioOutput output) {
        for(Integer index : outputs.keySet()) {
            if(outputs.get(index) == output) {
                return index;
            }
        }
        return -1;
    }

    public int inputIndex(AudioInput input) {
        for(Integer index : inputs.keySet()) {
            if(inputs.get(index) == input) {
                return index;
            }
        }
        return -1;
    }

    public void setAudio(AudioOutput output, AudioInput input, int volume) {
        Log.d("audio", "Setting output "+output.name+" to input "+(input == null ? "off" : input.name)+" at volume "+volume);

        if(input != output.currentInput) {
            sendChannelVol(output, 1);
        }
        sendChannelInput(output, input);
        sendChannelVol(output, volume);
        sendChannelMute(output, volume == 0);
    }

    public AudioOutput getOutput(String s) {
        for(AudioOutput o : outputs.values()) {
            if(o.name.equals(s)) {
                return o;
            }
        }
        return null;
    }

    public AudioOutput getOutput(int index) {
        return outputs.get(index);
    }

    public AudioInput getInput(int index) {
        return inputs.get(index);
    }

    public List<AudioOutput> inputUsers(AudioInput input) {
        List<AudioOutput> ret = new ArrayList();
        for(AudioOutput output : outputs.values()) {
            if(output.currentInput == input && output.currentVolume > 0) {
                ret.add(output);
            }
        }
        return ret;
    }

    public Map<Integer, AudioOutput> getOutputs() {
        return this.outputs;
    }
    public Map<Integer, AudioInput> getInputs() {
        return this.inputs;
    }

    public void setInputs(Map<Integer, AudioInput> inputs) {
        this.inputs = inputs;
        for(AudioInput input : inputs.values()) {
            input.availableOuputs.addAll(this.outputs.values());
        }
    }
}
