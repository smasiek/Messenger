package com.miloszmomot.messenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class MessengerObserver implements Observer {

    //TODO string zamienic na TempalateMethod chyba
    private String text;
    private Subject server;

    public MessengerObserver(Subject server){
        text="Pusty tekst";
        this.server=server;
        server.addObserver(this);
    }

    public void update(String wiadomosc) {
        text=wiadomosc;
    }

    public void send(String wiadomosc) {
        server.notifyObservers(wiadomosc);
    }

    public void getText(){
        System.out.println(text);
    }

    public void connectToSocket(String hostname,int port){

        //try (Socket socket = new Socket("127.0.0.1",9999)){
        try (Socket socket = new Socket(hostname,port)){
            InputStream input= socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String msg=reader.readLine();
            System.out.println(msg + " - to wiadomosc odebrana z socketu");

        }catch (UnknownHostException ex){
            System.out.println("Serwer nieosiągalny: " + ex.getMessage());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
