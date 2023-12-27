package com.example.finalproject.Utils;

import android.util.Log;

import androidx.annotation.NonNull;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

import timber.log.Timber;

public class  MyWebSocketClient {
    private WebSocketClient webSocketClient;

    public static String uglyJson = "REQUESTRESPONSE:{}";

    public WebSocketClient getClient() {
        return webSocketClient;
    }

    public void setServerUrl(@NonNull String serverUrl) throws URISyntaxException {
        URI serverUri = new URI(serverUrl);

        if (webSocketClient == null) {
            webSocketClient = new WebSocketClient(serverUri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Timber.d("Websocket on open");
                }

                @Override
                public void onMessage(String message) {
                    Timber.d("Websocket on message");
                    uglyJson = message;
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Timber.d("Websocket on close");
                }

                @Override
                public void onError(Exception ex) {
                    Timber.d("Websocket on error");
                }
            };
        }
    }

}
