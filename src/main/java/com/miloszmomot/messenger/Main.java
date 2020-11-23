package com.miloszmomot.messenger;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import java.io.*;
import java.net.*;
import java.sql.Time;

import static java.lang.Thread.sleep;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = new Server();

        MessengerObserver observer1 = new MessengerObserver(server);

        observer1.getText();

        MessengerObserver observer2 = new MessengerObserver(server);

        observer1.send("Nowa wiadomosc");

        observer1.getText();
        observer2.getText();
        System.out.println("ttest");
        server.startup();
        System.out.println("ttest");

        String hostname="localhost";
        int port=8080;
        observer1.connectToSocket(hostname,port);
        observer2.connectToSocket(hostname,port);

        observer1.getMessage();
        observer2.getMessage();

        //server.komenda();
        //sleep(1000);
        //observer1.getMessage();
        observer1.sendMessage("nowa wiadomosc");
        sleep(1000);
        observer1.getMessage();
        System.out.println("test");
        /*try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("asda1");
        observer1.getMessage();
        System.out.println("asda2");

        /*System.out.println("tescik");
        HelloEndpoint endpoint=new HelloEndpoint();
        System.out.println("tescik");
        WebSocketContainer webSocketContainer= ContainerProvider.getWebSocketContainer();
        System.out.println("tescik");
        try {
            webSocketContainer.connectToServer();
        } catch (DeploymentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        System.out.println("tescik");
        try {
            endpoint.sendMessage("hello from client");
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }


}
