package com.bendilts.iftttcontrol4audiobridge.audio.output;

import android.util.Log;

import com.bendilts.iftttcontrol4audiobridge.audio.AudioSystem;
import com.bendilts.iftttcontrol4audiobridge.audio.input.AudioInput;
import com.bendilts.iftttcontrol4audiobridge.audio.input.RadioInput;
import com.bendilts.iftttcontrol4audiobridge.audio.onkyo.Eiscp;
import com.bendilts.iftttcontrol4audiobridge.audio.onkyo.IscpCommands;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class OnkyoReceiver extends OutputDevice {

    private String ip;
    private IscpCommands commands = IscpCommands.getInstance();

    public void tune(final String station) {
        thread.queueCommand(new SendCommand() {
            @Override
            void go() {
                Eiscp device = new Eiscp(ip);
                device.sendCommand(IscpCommands.getTuneCmdStr((int)(Float.parseFloat(station)*10)));
                device.closeSocket();
            }
        });
    }

    abstract class SendCommand {
        abstract void go();
    }

    private class DeviceThread extends Thread {
        private int count = 0;
        private List<SendCommand> queue = new ArrayList();

        public void run() {
            while (true) {
                try {
                    Thread.sleep(100);

                    synchronized (queue) {
                        if (!queue.isEmpty()) {
                            queue.get(0).go();
                            queue.remove(0);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void queueCommand(SendCommand cmd) {
            synchronized(queue) {
                queue.add(cmd);
            }
        }
    }

    private DeviceThread thread = new DeviceThread();

    public OnkyoReceiver(String ip) {
        this.ip = ip;
        outputs.put(1, new AudioOutput(this, 9, "Theater", 0));

        thread.start();
    }

    public void sendChannelVol(final AudioOutput output, final int volume) {
        super.sendChannelVol(output, volume);

        if(outputIndex(output) != -1) {
            super.sendChannelVol(output, volume);

            thread.queueCommand(new SendCommand() {
                @Override
                void go() {
                    Eiscp device = new Eiscp(ip);
                    device.setVolume(finalVolume(output, volume));
                    if(volume > 0) {
                        if(device.sendQueryCommand(IscpCommands.POWER_QUERY).equals("00")) {
                            device.sendCommand(IscpCommands.POWER_ON);
                        }
                    }
                    device.sendCommand(IscpCommands.VOLUME_SET);
                    device.closeSocket();
                }
            });

            notifyListeners();
        }
    }

    public void sendChannelInput(AudioOutput output, final AudioInput input) {
        super.sendChannelInput(output, input);

        thread.queueCommand(new SendCommand() {
            @Override
            void go() {
                Eiscp device = new Eiscp(ip);
                if(input.name == "Chromecast") {
                    device.sendCommand(IscpCommands.SOURCE_COMPUTER);
                }
                if(input instanceof RadioInput) {
                    device.sendCommand(IscpCommands.SOURCE_FM);
                    if(AudioSystem.getInstance().radio.currentStations.containsKey(((RadioInput) input).tunerIndex)) {
                        tune(AudioSystem.getInstance().radio.currentStations.get(((RadioInput) input).tunerIndex));
                    }
                }
                device.closeSocket();
            }
        });

        notifyListeners();
    }

    public void sendChannelMute(AudioOutput output, final boolean mute) {
        super.sendChannelMute(output, mute);
        if(outputIndex(output) != -1) {
            super.sendChannelMute(output, mute);

            thread.queueCommand(new SendCommand() {
                @Override
                void go() {
                    Eiscp device = new Eiscp(ip);
                    device.sendCommand(mute ? IscpCommands.POWER_OFF : IscpCommands.POWER_ON);
                    device.closeSocket();
                }
            });

            notifyListeners();
        }
    }
}
