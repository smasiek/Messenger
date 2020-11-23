package com.miloszmomot.messenger;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.io.IOException;

public class HelloEndpoint extends Endpoint {
    private Session session;

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session=session;

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String s) {
                System.out.println("otrzymana z serwera wiadomosc: " + s);
            }
        });
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
}
