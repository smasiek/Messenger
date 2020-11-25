package com.miloszmomot.messenger;

import javax.websocket.*;
import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;

@ClientEndpoint
public class MessengerObserver implements Observer {

    //TODO string zamienic na TempalateMethod chyba
    private String text;
    private Subject server;

    public MessengerObserver() {
        text = "Pusty tekst";
//        server.addObserver(this);
    }

    public void update(String wiadomosc) {
        text = wiadomosc;
    }

    public void send(String wiadomosc) {
//        server.notifyObservers(wiadomosc);
    }

    public void getText() {
        System.out.println(text);
    }


    private ClientEndpointConfig clientConfig;
    private String user;
    Session session;
    @OnOpen
    public void connected(Session session, EndpointConfig clientConfig){
        this.clientConfig = (ClientEndpointConfig) clientConfig;
        this.user = session.getUserPrincipal().getName();

        this.session=session;
        System.out.println("User " + user + " connected to Chat room");
    }
    @OnMessage
    public void connected(String msg){
        System.out.println("Message from chat server: " + msg);
    }
    @OnClose
    public void disconnected(Session session, CloseReason reason){
        System.out.println("User "+ user + " disconnected as a result of "+ reason.getReasonPhrase());
    }
    @OnError
    public void disconnected(Session session, Throwable error){
        System.out.println("Error communicating with server: " + error.getMessage());
    }

}
