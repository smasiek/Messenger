package com.miloszmomot.messenger;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import javax.websocket.server.ServerEndpoint;
import java.io.*;
import java.net.*;
import java.sql.Time;

import static java.lang.Thread.sleep;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {


        MessengerObserver observer1 = new MessengerObserver();

        observer1.getText();

        MessengerObserver observer2 = new MessengerObserver();

        observer1.send("Nowa wiadomosc");

        observer1.getText();
        observer2.getText();


        Server server = new Server();
        System.out.println("tescik");
        MessengerObserver endpoint=new MessengerObserver();
        System.out.println("tescik");
        WebSocketContainer webSocketContainer= ContainerProvider.getWebSocketContainer();
        System.out.println("tescik");
        try {
            webSocketContainer.connectToServer(endpoint,new URI("ws://localhost:8080/test/ws/websocket"));
        } catch (DeploymentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        System.out.println("tescik");
        endpoint.send("wiadomosc nowa z klienta");

    }


}
