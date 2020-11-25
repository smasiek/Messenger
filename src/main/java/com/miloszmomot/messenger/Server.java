package com.miloszmomot.messenger;


import javax.websocket.*;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static sun.plugin2.main.client.LiveConnectSupport.shutdown;

@ServerEndpoint("/chat"
        //configurator = ChatEndpointConfigurator.class, //discussed later
       // decoders = JSONToChatObjectDecoder.class,
       // encoders = ChatObjectToJSONEncoder.class,
       // subprotocols = {"chat"}
)
public class Server{
    WebSocketContainer container;
    private Session session;

    @OnOpen
    public void onOpenCallback(Session session, EndpointConfig ec){
        try {
            container = ContainerProvider.getWebSocketContainer();
            container.setDefaultMaxSessionIdleTimeout(60000); //1 min. idle session timeout
            container.connectToServer(Server.class, URI.create("ws://messenger:8080")); //connecting to a websocket server
            this.session=session;
        } catch (DeploymentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//fetching the ServerContainer instance (from within a Server endpoint)
        ServerContainer container = (ServerContainer) session.getContainer();
    }

    @OnMessage
    public void handleChatMsg(String chat) {
        System.out.println("Got message - " + chat);
        if(this.session!= null && this.session.isOpen()){
            try{
                this.session.getBasicRemote().sendText("From server: " + chat);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
