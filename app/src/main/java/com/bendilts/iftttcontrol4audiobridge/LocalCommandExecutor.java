package com.bendilts.iftttcontrol4audiobridge;

public class LocalCommandExecutor extends CommandExecutor {
    @Override
    public void setAudio(AudioOutput output, AudioInput input, int volume) {
        AudioSystem.getInstance().receiver.setAudio(output, input, volume);
    }

    @Override
    public void tune(int tuner, String station) {
        AudioSystem.getInstance().radio.tune(tuner, station);
    }

    @Override
    public void updateFromMaster() {
        //Do nothing, we're the master
    }
}
