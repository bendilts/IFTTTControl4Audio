package com.bendilts.iftttcontrol4audiobridge;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public abstract class Control4Device {
    private final String ip;

    public Control4Device(String _ip) {
        ip = _ip;
        thread.start();
    }

    public interface DeviceListener {
        void onDeviceChange();
    }

    public List<DeviceListener> listeners = new ArrayList();
    protected void notifyListeners() {
        for(DeviceListener l : listeners) {
            l.onDeviceChange();
        }
    }

    //We can send one message each 100ms. Doing this in this separate thread guarantees we don't
    //pass that limit.
    private class DeviceThread extends Thread {
        private int count = 0;
        private List<String> queue = new ArrayList();

        public void run() {
            while(true) {
                try {
                    Thread.sleep(100);

                    synchronized(queue) {
                        if(!queue.isEmpty()) {
                            send(queue.get(0));
                            queue.remove(0);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void send(String cmd) throws IOException {
            String counter = String.format("0s%04x", count);
            count = (count + 1) % (16*16*16*16);

            cmd = counter + " " + cmd + "\r\n";
            Log.d("audio", cmd);
            DatagramSocket s = new DatagramSocket();
            byte[] message = cmd.getBytes();
            DatagramPacket p = new DatagramPacket(
                    message,
                    message.length,
                    InetAddress.getByName(ip),
                    8750
            );
            s.send(p);
        }

        private void queueCommand(String cmd) {
            synchronized(queue) {
                queue.add(cmd);
            }
        }
    }

    private DeviceThread thread = new DeviceThread();

    protected void sendToDevice(String cmd) {
        thread.queueCommand(cmd);
    }
}
