package com.bendilts.iftttcontrol4audiobridge;

import com.bendilts.iftttcontrol4audiobridge.audio.input.AudioInput;
import com.bendilts.iftttcontrol4audiobridge.audio.output.AudioOutput;

public abstract class CommandExecutor {
    static final String masterIP = "192.168.1.34";
    static final String myIP = NetUtils.getIPAddress(true);
    static final boolean isMaster = myIP.equals(masterIP);

    private static CommandExecutor instance;
    public static CommandExecutor getInstance() {
        if(instance == null) {
            if(isMaster) {
                instance = new LocalCommandExecutor();
            } else {
                instance = new RemoteCommandExecutor(masterIP);
            }
        }
        return instance;
    }

    public abstract void setAudio(AudioOutput output, AudioInput input, int volume);
    public abstract void tune(int tuner, String station);
    public abstract void updateFromMaster();
}
