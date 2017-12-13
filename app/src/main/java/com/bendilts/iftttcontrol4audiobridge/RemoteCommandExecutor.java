package com.bendilts.iftttcontrol4audiobridge;

import android.os.AsyncTask;
import android.util.Log;

import com.bendilts.iftttcontrol4audiobridge.audio.AudioSystem;
import com.bendilts.iftttcontrol4audiobridge.audio.input.AudioInput;
import com.bendilts.iftttcontrol4audiobridge.audio.output.AudioOutput;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RemoteCommandExecutor extends CommandExecutor {
    private String serverIP;
    public RemoteCommandExecutor(String ip) {
        serverIP = ip;
    }

    private class HTTPTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String response = "";

            try {
                Socket sock = new Socket(serverIP, 8080);
                PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

                Log.i("http", "Sending request to master at "+serverIP);
                for(String p : params) {
                    Log.i("http", p);
                    out.println(p);
                }

                String line;
                boolean passedEmptyLine = false;
                while ((line = in.readLine()) != null) {
                    if(passedEmptyLine) {
                        response += line;
                    }

                    if(line.equals("")) {
                        passedEmptyLine = true;
                    }
                    Log.i("http", line);
                }
            }catch(Exception e) {
                e.printStackTrace();
            }

            Log.i("http", "Response body: "+response);
            return response;
        }
    }

    private void post(String body) {
        new HTTPTask().execute(
            String.format("POST http://%s:8080/audio HTTP/1.1", serverIP),
            String.format("Content-length: %d", body.length()),
            "",
            body
        );
    }

    @Override
    public void setAudio(AudioOutput output, AudioInput input, int volume) {
        post(String.format("a %d %d %d", output.id, input.id, volume));
        output.currentInput = input;
        output.currentVolume = volume;
        output.device.notifyListeners();
    }

    @Override
    public void tune(int tuner, String station) {
        post(String.format("t %d %s", tuner, station));
        AudioSystem.getInstance().radio.currentStations.put(tuner, station);
    }

    @Override
    public void updateFromMaster() {
        new HTTPTask() {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                AudioSystem.getInstance().deserialize(s);
            }
        }.execute(String.format("GET http://%s:8080/audio HTTP/1.1", serverIP));

    }
}
