package com.example.finalproject.Utils;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class  MyWebSocketClient extends WebSocketClient {

    public static String uglyJson = "REQUESTRESPONSE:{}";
    public MyWebSocketClient(String serverUrl) throws URISyntaxException {
        super(new URI(serverUrl));
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        Log.d("WebSocket","On open");

    }
    @Override
    public void onMessage(String message) {
        Log.d("WebSocket","On message");
        this.uglyJson = message;
        Log.d("WebSocket",uglyJson);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d("WebSocket","On close");
    }

    @Override
    public void onError(Exception ex) {
        Log.d("WebSocket","On error");
    }
}