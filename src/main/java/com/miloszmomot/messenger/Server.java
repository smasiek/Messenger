package com.miloszmomot.messenger;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static sun.plugin2.main.client.LiveConnectSupport.shutdown;

public class Server extends Subject {
    private ArrayList<Observer> observers=new ArrayList<Observer>();

    public Server() throws IOException {
        serverSocket = new ServerSocket(8080);
    }

    public void addObserver(Observer observer){
        observers.add(observer);
    }
    public void removeObserver(Observer observer){
        int observerIndex=observers.indexOf(observer);
        System.out.println("Observer number " + observerIndex + " removed.");

        observers.remove(observer);
    }
    //TODO zmienic string na MessageTemplate
    public void notifyObservers(String string){
        for (Observer observer : observers) {
            observer.update(string);
        }
    }

    private Exception serverError=null;
    ExecutorService singleThreadManager;
    ServerThread serverThread;
    ServerSocket serverSocket;

    public void run(){
        try {
            while (true) {
                //Socket socket = serverSocket.accept();
                //Po moich zmianach:
                serverThread=new ServerThread(serverSocket.accept());

                // to bylo dobre w sumie przez chwile: singleThreadManager.execute(serverThread);
                singleThreadManager.execute(serverThread);
            }
        } catch (IOException ex) {
            serverError = ex;
            // stop = true;
            System.out.println("Zamykam watek");
            shutdown(); // shutdown cleanly after exception
        }
    }

    public void startup(){
        singleThreadManager= Executors.newSingleThreadExecutor();
        start();
    }

    public void komenda(){
        //serverThread.newText();
    }

   /* public void komenda() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                //Po moich zmianach:
                serverThread=new ServerThread(socket);
                singleThreadManager.execute(serverThread);
            }
        } catch (IOException ex) {
            serverError = ex;
            // stop = true;
            System.out.println("Zamykam watek");
            shutdown(); // shutdown cleanly after exception
        }
    }*/

}
