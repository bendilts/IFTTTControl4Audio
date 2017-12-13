package com.bendilts.iftttcontrol4audiobridge;

import com.bendilts.iftttcontrol4audiobridge.audio.AudioSystem;
import com.bendilts.iftttcontrol4audiobridge.audio.input.AudioInput;
import com.bendilts.iftttcontrol4audiobridge.audio.input.RadioInput;
import com.bendilts.iftttcontrol4audiobridge.audio.output.AudioOutput;
import com.bendilts.iftttcontrol4audiobridge.audio.output.OnkyoReceiver;

public class LocalCommandExecutor extends CommandExecutor {
    @Override
    public void setAudio(AudioOutput output, AudioInput input, int volume) {
        output.device.setAudio(output, input, volume);
    }

    @Override
    public void tune(int tuner, String station) {
        AudioSystem.getInstance().radio.tune(tuner, station);

        OnkyoReceiver r = AudioSystem.getInstance().basementReceiver;
        AudioInput i = r.getOutput(1).currentInput;
        if(i instanceof RadioInput) {
            RadioInput radio = (RadioInput)i;
            if(radio.tunerIndex == tuner) {
                AudioSystem.getInstance().basementReceiver.tune(station);
            }
        }
    }

    @Override
    public void updateFromMaster() {
        //Do nothing, we're the master
    }
}
