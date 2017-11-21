package com.bendilts.iftttcontrol4audiobridge;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HTTPServer {
    private AudioSystem system;
    private static HTTPServer instance;

    public static HTTPServer getInstance() {
        if(instance == null) {
            instance = new HTTPServer();
            instance.listen(8080);
        }
        return instance;
    }

    public HTTPServer() {
        system = AudioSystem.getInstance();
    }

    private class ListenTask extends AsyncTask<Integer, Integer, Long> {
        ServerSocket listenSocket;

        private Map<String,String> parseQuery(String request) {
            Map<String,String> ret = new HashMap();

            String url = request.split(" ")[1];
            String rawQuery = url.split("\\?", 2)[1];
            for(String param : rawQuery.split("&")) {
                String[] parts = param.split("=", 2);
                ret.put(parts[0], URLDecoder.decode(parts[1]));
            }

            return ret;
        }

        private void handleCommand(String body) {
            String info;

            try {
                String[] parts = body.split(" ");
                int p = 0;
                while (p < parts.length) {
                    String command = parts[p++];
                    switch (command) {
                        case "a":
                            system.receiver.setAudio(
                                    system.receiver.getOutput(Integer.parseInt(parts[p])),
                                    system.receiver.getInput(Integer.parseInt(parts[p + 1])),
                                    Integer.parseInt(parts[p + 2])
                            );
                            p += 3;
                            break;
                        case "t":
                            system.radio.tune(
                                    Integer.parseInt(parts[p]),
                                    parts[p+1]
                            );
                            p += 2;
                            break;
                        case "m":
                            system.receiver.setAudio(
                                    system.receiver.getOutput(Integer.parseInt(parts[p])),
                                    null,
                                    0
                            );
                            p += 1;
                            break;
                        case "l":
                            system.listen(
                                    system.receiver.getInput(Integer.parseInt(parts[p])),
                                    TextUtils.join(" ", Arrays.copyOfRange(parts, p + 1, parts.length))
                            );
                            p = parts.length;
                            break;
                        case "v":
                            int volume = 0;
                            try {
                                volume = Integer.parseInt(parts[p]);
                                info = TextUtils.join(" ", Arrays.copyOfRange(parts, p + 1, parts.length));
                            } catch(Exception e) {
                                //No numeric volume at the beginning of the message. Just parse out
                                //any named volumes.
                                info = TextUtils.join(" ", Arrays.copyOfRange(parts, p, parts.length));
                                volume = system.volumeFromInfo(info);
                            }

                            List<AudioOutput> outputs = system.outputsFromInfo(info);
                            if(outputs.isEmpty()) {
                                outputs = system.receiver.outputs;
                            }

                            for(AudioOutput output : outputs) {
                                if(output.currentVolume > 0) {
                                    system.receiver.sendChannelVol(output, volume);
                                }
                            }
                            p = parts.length;
                            break;
                        case "k":
                            info = TextUtils.join(" ", Arrays.copyOfRange(parts, p, parts.length));
                            for(AudioOutput output : system.outputsFromInfo(info)) {
                                system.receiver.setAudio(output, null, 0);
                            }
                            p = parts.length;
                            break;
                    }
                }
            }
            catch(Exception e) {
                Log.d("Exception caught", e.getMessage());
            }
        }

        @Override
        protected Long doInBackground(Integer... params) {
            try {
                int port = params[0];
                listenSocket = new ServerSocket(port);

                while(true) {
                    try {
                        Socket connectionSocket = listenSocket.accept();
                        Log.i("http", "Connection received");
                        BufferedReader inFromClient =
                                new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                        DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

                        //Read until we find the empty line that separates headers from body
                        String data;

                        //The HTTP request method/url/version
                        data = inFromClient.readLine();
                        Log.i("http", data);
                        String[] parts = data.split(" ");
                        String method = parts[0].toUpperCase();

                        if(method.equals("POST")) {
                            int contentLength = 0;
                            do {
                                data = inFromClient.readLine();
                                parts = data.split(":", 2);
                                if (parts[0].toLowerCase().trim().equals("content-length")) {
                                    contentLength = Integer.parseInt(parts[1].trim());
                                    Log.i("http", "content-length: " + contentLength);
                                }
                                Log.i("http", data);
                            } while (data.length() > 0);

                            //Then read the body.
                            String command;
                            if (contentLength > 0) {
                                char[] buf = new char[contentLength];
                                inFromClient.read(buf, 0, contentLength);
                                command = new String(buf);
                            } else {
                                command = inFromClient.readLine();
                            }
                            Log.i("http", "command: " + command);

                            Log.i("http", "Running command: " + command);
                            handleCommand(command);

                            String response = "HTTP/1.1 204 No Content\r\n";
                            outToClient.writeBytes(response);
                        } else if(method.equals("GET")) {
                            outToClient.writeBytes("HTTP/1.1 200 OK\r\n");
                            outToClient.writeBytes("\r\n");
                            outToClient.writeBytes(AudioSystem.getInstance().serialize());
                        }

                        connectionSocket.close();
                        Log.i("http", "Connection closed");

                    } catch(Exception e) {
                        Log.e("http", "Exception accepting/processing incoming connection: "+e.getMessage());
                    }
                }
            } catch(Exception e) {
                Log.e("http", "Exception listening to incoming connections: "+e.getMessage());
            }
            return null;
        }
    }

    public void listen(int port) {
        Log.i("http", "Starting listen task");
        new ListenTask().execute(port);
    }
}
