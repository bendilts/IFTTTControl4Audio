package com.bendilts.iftttcontrol4audiobridge.audio;

import android.content.Context;
import android.content.SharedPreferences;

import com.bendilts.iftttcontrol4audiobridge.CommandExecutor;
import com.bendilts.iftttcontrol4audiobridge.R;
import com.bendilts.iftttcontrol4audiobridge.audio.control4.Control4Radio;
import com.bendilts.iftttcontrol4audiobridge.audio.input.AudioInput;
import com.bendilts.iftttcontrol4audiobridge.audio.input.RadioInput;
import com.bendilts.iftttcontrol4audiobridge.audio.output.AudioOutput;
import com.bendilts.iftttcontrol4audiobridge.audio.output.AudioZone;
import com.bendilts.iftttcontrol4audiobridge.audio.output.Control4Receiver;
import com.bendilts.iftttcontrol4audiobridge.audio.output.OnkyoReceiver;
import com.bendilts.iftttcontrol4audiobridge.audio.output.OutputDevice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioSystem {
    private static AudioSystem instance;

    public static AudioSystem getInstance(Context context) {
        AudioSystem system = getInstance();
        SharedPreferences prefs = context.getSharedPreferences("audio", Context.MODE_PRIVATE);
        system.context = context;
        system.setLocalOutput(system.outputById(prefs.getInt("localOutput", 1)));
        return system;
    }

    public static AudioSystem getInstance() {
        if(instance == null) {
            instance = new AudioSystem();
        }
        return instance;
    }

    private Control4Receiver control4Receiver = new Control4Receiver();
    public OnkyoReceiver theaterReceiver = new OnkyoReceiver("192.168.1.17");
    public Control4Radio radio = new Control4Radio();

    private List<AudioZone> zones = new ArrayList();
    private Map<String, Integer> volumeNames = new HashMap();

    private AudioOutput localOutput;
    private Context context = null;

    public AudioOutput getLocalOutput() {
        return this.localOutput;
    }

    public void setLocalOutput(AudioOutput output) {
        this.localOutput = output;
        if(this.context != null) {
            SharedPreferences prefs = context.getSharedPreferences("audio", Context.MODE_PRIVATE);
            prefs.edit().putInt("localOutput", output.id).commit();
        }
    }

    AudioInput radio1 = new RadioInput(1, "FM Radio 1", "radio", -5, 1);
    AudioInput radio2 = new RadioInput(2, "FM Radio 2", "notasearchablethingonlyviatouchscreen", -5, 2);
    AudioInput tvAudio = new AudioInput(3, "TV Audio", "tv", 10, R.drawable.tv);
    AudioInput chromecast = new AudioInput(4, "Chromecast", "chromecast", 0, R.drawable.chromecast);
    AudioInput mic = new AudioInput(5, "Microphone", "mic", 5, R.drawable.microphone);
    AudioInput bluetooth = new AudioInput(6, "Bluetooth", "bluetooth", 0, R.drawable.bluetooth);

    //All inputs and outputs system-wide:
    public AudioInput[] inputs = new AudioInput[] {radio1, radio2, tvAudio, chromecast, mic, bluetooth};
    public List<AudioOutput> outputs = new ArrayList();
    public List<OutputDevice> outputDevices = new ArrayList();

    public AudioInput inputById(int id) {
        for(AudioInput input : inputs) {
            if(input.id == id) {
                return input;
            }
        }
        return null;
    }

    public AudioOutput outputById(int id) {
        for(AudioOutput output : outputs) {
            if(output.id == id) {
                return output;
            }
        }
        return null;
    }

    private AudioSystem() {
        instance = this;

        outputDevices.add(control4Receiver);
        outputDevices.add(theaterReceiver);

        outputs.addAll(control4Receiver.getOutputs().values());

        Map<Integer, AudioInput> c4Inputs = new HashMap();
        c4Inputs.put(1, radio1);
        c4Inputs.put(2, radio2);
        c4Inputs.put(3, tvAudio);
        c4Inputs.put(4, chromecast);
        c4Inputs.put(5, mic);
        c4Inputs.put(6, bluetooth);
        control4Receiver.setInputs(c4Inputs);

        Map<Integer, AudioInput> theaterInputs = new HashMap();
        theaterInputs.put(1, chromecast);
        theaterInputs.put(2, radio1);
        theaterInputs.put(3, radio2);
        theaterReceiver.setInputs(theaterInputs);

        localOutput = outputs.get(0);

        zones.add(new AudioZone("master bed", new AudioOutput[] {
                this.control4Receiver.getOutput("Master Bedroom"),
        }));

        zones.add(new AudioZone("master bath", new AudioOutput[] {
                this.control4Receiver.getOutput("Master Bathroom"),
        }));

        zones.add(new AudioZone("office", new AudioOutput[] {
                this.control4Receiver.getOutput("Office"),
        }));

        zones.add(new AudioZone("front room", new AudioOutput[] {
                this.control4Receiver.getOutput("Front Room"),
        }));

        zones.add(new AudioZone("deck", new AudioOutput[] {
                this.control4Receiver.getOutput("Deck"),
        }));

        zones.add(new AudioZone("kitchen", new AudioOutput[] {
                this.control4Receiver.getOutput("Kitchen"),
        }));

        zones.add(new AudioZone("family room", new AudioOutput[] {
                this.control4Receiver.getOutput("Kitchen"),
        }));

        zones.add(new AudioZone("library", new AudioOutput[] {
                this.control4Receiver.getOutput("Library"),
        }));

        zones.add(new AudioZone("patio", new AudioOutput[] {
                this.control4Receiver.getOutput("Patio"),
        }));

        zones.add(new AudioZone("hot tub", new AudioOutput[] {
                this.control4Receiver.getOutput("Patio"),
        }));

        zones.add(new AudioZone("main floor", new AudioOutput[] {
                this.control4Receiver.getOutput("Master Bathroom"),
                this.control4Receiver.getOutput("Master Bedroom"),
                this.control4Receiver.getOutput("Front Room"),
                this.control4Receiver.getOutput("Office"),
                this.control4Receiver.getOutput("Kitchen"),
        }));

        zones.add(new AudioZone("indoors", new AudioOutput[] {
                this.control4Receiver.getOutput("Master Bathroom"),
                this.control4Receiver.getOutput("Master Bedroom"),
                this.control4Receiver.getOutput("Front Room"),
                this.control4Receiver.getOutput("Office"),
                this.control4Receiver.getOutput("Kitchen"),
                this.control4Receiver.getOutput("Library"),
        }));

        zones.add(new AudioZone("inside", new AudioOutput[] {
                this.control4Receiver.getOutput("Master Bathroom"),
                this.control4Receiver.getOutput("Master Bedroom"),
                this.control4Receiver.getOutput("Front Room"),
                this.control4Receiver.getOutput("Office"),
                this.control4Receiver.getOutput("Kitchen"),
                this.control4Receiver.getOutput("Library"),
        }));

        zones.add(new AudioZone("in doors", new AudioOutput[] {
                this.control4Receiver.getOutput("Master Bathroom"),
                this.control4Receiver.getOutput("Master Bedroom"),
                this.control4Receiver.getOutput("Front Room"),
                this.control4Receiver.getOutput("Office"),
                this.control4Receiver.getOutput("Kitchen"),
                this.control4Receiver.getOutput("Library"),
        }));

        zones.add(new AudioZone("in side", new AudioOutput[] {
                this.control4Receiver.getOutput("Master Bathroom"),
                this.control4Receiver.getOutput("Master Bedroom"),
                this.control4Receiver.getOutput("Front Room"),
                this.control4Receiver.getOutput("Office"),
                this.control4Receiver.getOutput("Kitchen"),
                this.control4Receiver.getOutput("Library"),
        }));

        zones.add(new AudioZone("upstairs", new AudioOutput[] {
                this.control4Receiver.getOutput("Library"),
        }));

        zones.add(new AudioZone("outside", new AudioOutput[] {
                this.control4Receiver.getOutput("Deck"),
                this.control4Receiver.getOutput("Patio"),
        }));

        zones.add(new AudioZone("everywhere", new AudioOutput[] {
                this.control4Receiver.getOutput("Master Bathroom"),
                this.control4Receiver.getOutput("Master Bedroom"),
                this.control4Receiver.getOutput("Front Room"),
                this.control4Receiver.getOutput("Office"),
                this.control4Receiver.getOutput("Kitchen"),
                this.control4Receiver.getOutput("Library"),
                this.control4Receiver.getOutput("Deck"),
                this.control4Receiver.getOutput("Patio"),
        }));

        volumeNames.put("low", 25);
        volumeNames.put("quiet", 25);
        volumeNames.put("soft", 25);
        volumeNames.put("normal", 40);
        volumeNames.put("regular", 40);
        volumeNames.put("loud", 60);
    }

    private List<AudioInput> inputsFromInfo(String info) {
        List<AudioInput> ret = new ArrayList();
        for(AudioInput input : inputs) {
            if(info.toLowerCase().contains(input.searchName)) {
                ret.add(input);
            }
        }
        return ret;
    }

    public List<AudioOutput> inputUsers(AudioInput input) {
        List<AudioOutput> ret = new ArrayList();
        for(OutputDevice device : outputDevices) {
            ret.addAll(device.inputUsers(input));
        }
        return ret;
    }

    public List<AudioOutput> outputsFromInfo(String info) {
        List<AudioOutput> ret = new ArrayList();
        for(AudioZone zone : zones){
            if(info.toLowerCase().contains(zone.name)) {
                for(AudioOutput output : zone.outputs) {
                    if(!ret.contains(output)){
                        ret.add(output);
                    }
                }
            }
        }

        for(AudioInput input : inputsFromInfo(info)) {
            for(AudioOutput output : outputs) {
                if(output.currentInput == input && !ret.contains(output)) {
                    ret.add(output);
                }
            }
        }
        return ret;
    }

    public int volumeFromInfo(String info) {
        for(String name : volumeNames.keySet()) {
            if(info.toLowerCase().contains(name)) {
                return volumeNames.get(name);
            }
        }
        return 40;
    }

    public void listen(AudioInput input, String info) throws IOException {
        int volume = volumeFromInfo(info);
        for(AudioOutput output : outputsFromInfo(info)) {
            CommandExecutor.getInstance().setAudio(output, input, volume);
        }
    }

    public String serialize() {
        StringBuilder ret = new StringBuilder();
        for(AudioOutput output : outputs) {
            ret.append(String.format("a %d %d %d ",
                    output.id,
                    output.currentInput == null ? 0 : output.currentInput.id,
                    output.currentVolume
            ));
        }

        ret.append(String.format("t 1 %s ", radio.currentStations.get(1)));
        ret.append(String.format("t 2 %s ", radio.currentStations.get(2)));
        return ret.toString();
    }

    public void deserialize(String state) {
        String[] parts = state.split(" ");
        int p = 0;
        while (p < parts.length) {
            String command = parts[p++];
            switch (command) {
                case "a":
                    AudioOutput output = control4Receiver.getOutput(Integer.parseInt(parts[p]));
                    AudioInput input = control4Receiver.getInput(Integer.parseInt(parts[p + 1]));
                    output.currentInput = input;
                    output.currentVolume = Integer.parseInt(parts[p + 2]);
                    control4Receiver.notifyListeners();
                    p += 3;
                    break;
                case "t":
                    radio.currentStations.put(Integer.parseInt(parts[p]), parts[p+1]);
                    p += 2;
                    break;
            }
        }
    }
}
