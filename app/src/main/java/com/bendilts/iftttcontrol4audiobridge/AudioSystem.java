package com.bendilts.iftttcontrol4audiobridge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioSystem {
    static final String masterIP = "192.168.1.34";
    static final boolean isMaster = NetUtils.getIPAddress(true).equals(masterIP);
    static AudioSystem instance;

    public Control4Receiver receiver = new Control4Receiver();
    public Control4Radio radio = new Control4Radio();

    private List<AudioZone> zones = new ArrayList();
    private Map<String, Integer> volumeNames = new HashMap();

    AudioSystem() {
        instance = this;

        zones.add(new AudioZone("master bed", new AudioOutput[] {
                this.receiver.getOutput("Master Bedroom"),
        }));

        zones.add(new AudioZone("master bath", new AudioOutput[] {
                this.receiver.getOutput("Master Bathroom"),
        }));

        zones.add(new AudioZone("office", new AudioOutput[] {
                this.receiver.getOutput("Office"),
        }));

        zones.add(new AudioZone("front room", new AudioOutput[] {
                this.receiver.getOutput("Front Room"),
        }));

        zones.add(new AudioZone("deck", new AudioOutput[] {
                this.receiver.getOutput("Deck"),
        }));

        zones.add(new AudioZone("kitchen", new AudioOutput[] {
                this.receiver.getOutput("Kitchen"),
        }));

        zones.add(new AudioZone("family room", new AudioOutput[] {
                this.receiver.getOutput("Kitchen"),
        }));

        zones.add(new AudioZone("library", new AudioOutput[] {
                this.receiver.getOutput("Library"),
        }));

        zones.add(new AudioZone("patio", new AudioOutput[] {
                this.receiver.getOutput("Patio"),
        }));

        zones.add(new AudioZone("hot tub", new AudioOutput[] {
                this.receiver.getOutput("Patio"),
        }));

        zones.add(new AudioZone("main floor", new AudioOutput[] {
                this.receiver.getOutput("Master Bathroom"),
                this.receiver.getOutput("Master Bedroom"),
                this.receiver.getOutput("Front Room"),
                this.receiver.getOutput("Office"),
                this.receiver.getOutput("Kitchen"),
        }));

        zones.add(new AudioZone("indoors", new AudioOutput[] {
                this.receiver.getOutput("Master Bathroom"),
                this.receiver.getOutput("Master Bedroom"),
                this.receiver.getOutput("Front Room"),
                this.receiver.getOutput("Office"),
                this.receiver.getOutput("Kitchen"),
                this.receiver.getOutput("Library"),
        }));

        zones.add(new AudioZone("inside", new AudioOutput[] {
                this.receiver.getOutput("Master Bathroom"),
                this.receiver.getOutput("Master Bedroom"),
                this.receiver.getOutput("Front Room"),
                this.receiver.getOutput("Office"),
                this.receiver.getOutput("Kitchen"),
                this.receiver.getOutput("Library"),
        }));

        zones.add(new AudioZone("in doors", new AudioOutput[] {
                this.receiver.getOutput("Master Bathroom"),
                this.receiver.getOutput("Master Bedroom"),
                this.receiver.getOutput("Front Room"),
                this.receiver.getOutput("Office"),
                this.receiver.getOutput("Kitchen"),
                this.receiver.getOutput("Library"),
        }));

        zones.add(new AudioZone("in side", new AudioOutput[] {
                this.receiver.getOutput("Master Bathroom"),
                this.receiver.getOutput("Master Bedroom"),
                this.receiver.getOutput("Front Room"),
                this.receiver.getOutput("Office"),
                this.receiver.getOutput("Kitchen"),
                this.receiver.getOutput("Library"),
        }));

        zones.add(new AudioZone("upstairs", new AudioOutput[] {
                this.receiver.getOutput("Library"),
        }));

        zones.add(new AudioZone("outside", new AudioOutput[] {
                this.receiver.getOutput("Deck"),
                this.receiver.getOutput("Patio"),
        }));

        zones.add(new AudioZone("everywhere", new AudioOutput[] {
                this.receiver.getOutput("Master Bathroom"),
                this.receiver.getOutput("Master Bedroom"),
                this.receiver.getOutput("Front Room"),
                this.receiver.getOutput("Office"),
                this.receiver.getOutput("Kitchen"),
                this.receiver.getOutput("Library"),
                this.receiver.getOutput("Deck"),
                this.receiver.getOutput("Patio"),
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
        for(AudioInput input : receiver.inputs) {
            if(info.toLowerCase().contains(input.searchName)) {
                ret.add(input);
            }
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
            for(AudioOutput output : receiver.outputs) {
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
            receiver.setAudio(output, input, volume);
        }
    }
}
