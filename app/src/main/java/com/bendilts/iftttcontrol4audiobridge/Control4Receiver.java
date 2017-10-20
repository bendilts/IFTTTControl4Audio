package com.bendilts.iftttcontrol4audiobridge;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Control4Receiver extends Control4Device {
    public List<AudioInput> inputs = new ArrayList();
    public List<AudioOutput> outputs = new ArrayList();

    public Control4Receiver() {
        super("192.168.1.39");

        inputs.add(new RadioInput(1, "FM Radio 1", "radio", -5, 1));
        inputs.add(new RadioInput(2, "FM Radio 2", "notasearchablethingonlyviatouchscreen", -5, 2));
        inputs.add(new AudioInput(3, "TV Audio", "tv", 10, R.drawable.tv));
        inputs.add(new AudioInput(4, "Chromecast", "chromecast", 0, R.drawable.chromecast));
        inputs.add(new AudioInput(5, "Microphone", "mic", 5, R.drawable.microphone));
        inputs.add(new AudioInput(6, "Bluetooth", "bluetooth", 0, R.drawable.bluetooth));

        outputs.add(new AudioOutput(6, "Kitchen", 5));
        outputs.add(new AudioOutput(3, "Office", 0));
        outputs.add(new AudioOutput(7, "Library", 5));
        outputs.add(new AudioOutput(4, "Front Room", 0));
        outputs.add(new AudioOutput(1, "Master Bedroom", 0));
        outputs.add(new AudioOutput(2, "Master Bathroom", 0));
        outputs.add(new AudioOutput(5, "Deck", 10));
        outputs.add(new AudioOutput(8, "Patio", 10));
    }

    public void sendChannelVol(AudioOutput output, int volume) {
        output.currentVolume = Math.max(0, Math.min(100, volume));
        volume += output.gain + (output.currentInput == null ? 0 : output.currentInput.gain);
        volume = Math.max(1, Math.min(80, volume)); //Over 80 may damage my speakers
        sendToDevice(String.format("c4.amp.chvol %02x %02x", output.index, volume + 155));
        notifyListeners();
    }

    private void sendChannelInput(AudioOutput output, AudioInput input) {
        if(input != output.currentInput) {
            output.currentInput = input;

            if(output.currentVolume > 0) {
                sendChannelVol(output, output.currentVolume);
            }
        }
        sendToDevice(String.format("c4.amp.out %02x %02x", output.index, input == null ? 0 : input.index));
        notifyListeners();
    }

    public void sendChannelMute(AudioOutput output, boolean mute) {
        sendToDevice(String.format("c4.amp.mute %02x %02x", output.index, mute ? 1 : 0));
        notifyListeners();
    }

    public void setAudio(AudioOutput output, AudioInput input, int volume) {
        Log.d("audio", "Setting output "+output.name+" to input "+(input == null ? "off" : input.name)+" at volume "+volume);

        if(input != output.currentInput) {
            sendChannelMute(output, true);
        }
        sendChannelInput(output, input);
        sendChannelVol(output, volume);
        sendChannelMute(output, volume == 0);
    }

    public AudioOutput getOutput(String s) {
        for(AudioOutput o : outputs) {
            if(o.name.equals(s)) {
                return o;
            }
        }
        return null;
    }

    public AudioOutput getOutput(int index) {
        for(AudioOutput o : outputs) {
            if(o.index == index) {
                return o;
            }
        }
        return null;
    }

    public AudioInput getInput(int index) {
        for(AudioInput i : inputs) {
            if(i.index == index) {
                return i;
            }
        }
        return null;
    }

    public List<AudioOutput> inputUsers(AudioInput input) {
        List<AudioOutput> ret = new ArrayList();
        for(AudioOutput output : outputs) {
            if(output.currentInput == input && output.currentVolume > 0) {
                ret.add(output);
            }
        }
        return ret;
    }
}
