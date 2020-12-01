package com.miloszmomot.messenger;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import static sun.plugin2.main.client.LiveConnectSupport.shutdown;

public class Server extends Subject {
    private ArrayList<ServerClientThread> observerThreads = new ArrayList<>();

    public Server() {
        this.start();
    }

    @Override
    public void addObserver(ServerSocket serverSocket) {
        try {
            Socket socket = serverSocket.accept();
            ServerClientThread serverClientThread = new ServerClientThread(socket, this);
            serverClientThread.start();
            observerThreads.add(serverClientThread);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeObserver(ServerClientThread serverClientThread) {
        int observerIndex = observerThreads.indexOf(serverClientThread);
        System.out.println("Observer number " + observerIndex + " removed.");

        observerThreads.remove(serverClientThread);
    }

    public void notifyObservers(byte[] string) {
        for (ServerClientThread observerThread : observerThreads) {
            observerThread.messagePropagation(string);
        }
    }

    public int getObserverIndex(ServerClientThread serverClientThread) {
        return observerThreads.indexOf(serverClientThread);
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(5657)) {
            System.out.println("Server launched\n");
            while (true) {
                addObserver(serverSocket);
                System.out.println("Client has to a server connected");
            }
        } catch (IOException ex) {
            System.out.println("Zamykam watek");
            shutdown(); // shutdown cleanly after exception
        }
    }

}
