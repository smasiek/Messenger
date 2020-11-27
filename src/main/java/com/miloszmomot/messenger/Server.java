package com.miloszmomot.messenger;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

import static sun.plugin2.main.client.LiveConnectSupport.shutdown;

public class Server extends Subject {
    private ArrayList<ServerClientThread> observerThreads=new ArrayList<ServerClientThread>();



    public Server() throws IOException {
        //serverSocket = new ServerSocket(8080);
    }
    /*@Override
    public void addObserver(Observering observer){
        observers.add(observer);
    }*/

    /* public void removeObserver(Observering observer){
         int observerIndex=observers.indexOf(observer);
         System.out.println("Observer number " + observerIndex + " removed.");

         observers.remove(observer);
     }*/
    //TODO zmienic string na MessageTemplate
    public void notifyObservers(String string){
        for (ServerClientThread observerThread : observerThreads) {
            observerThread.messagePropagation(string);
        }
    }

    private Exception serverError=null;
    ExecutorService singleThreadManager;
    ServerClientThread serverClientThread;
    ServerSocket serverSocket;

    public void run(){
        try(ServerSocket serverSocket =  new ServerSocket(8080)) {
            while (true) {
                Socket socket = serverSocket.accept();
                //Po moich zmianach:
                ServerClientThread serverClientThread = new ServerClientThread(socket,this);
                serverClientThread.start();
                observerThreads.add(serverClientThread);

                System.out.println("Client has to a server connected");

                // to bylo dobre w sumie przez chwile: singleThreadManager.execute(serverThread);
                //singleThreadManager.execute(serverThread);
            }
        } catch (IOException ex) {
            serverError = ex;
            // stop = true;
            System.out.println("Zamykam watek");
            shutdown(); // shutdown cleanly after exception
        }
    }

   /* public void startup(){
        singleThreadManager= Executors.newSingleThreadExecutor();
        start();
    }*/

    /*public void komenda(){
        //serverThread.newText();
    }*/


}
