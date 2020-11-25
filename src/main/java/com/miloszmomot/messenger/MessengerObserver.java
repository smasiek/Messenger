package com.miloszmomot.messenger;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class MessengerObserver implements Observer {

    //TODO string zamienic na TempalateMethod chyba
    private String text;
    private Subject server;

    public MessengerObserver(Subject server) {
        text = "Pusty tekst";
        this.server = server;
        server.addObserver(this);
    }

    public void update(String wiadomosc) {
        text = wiadomosc;
    }

    public void send(String wiadomosc) {
        server.notifyObservers(wiadomosc);
    }

    public void getText() {
        System.out.println(text);
    }


    InputStream input;
    BufferedReader bufferedReader;
    String msg;
    Socket socket;
    OutputStream output;
    PrintWriter writer;
    InputStreamReader inputStreamReader;
    public void connectToSocket(String hostname, int port) {

        //try (Socket socket = new Socket("127.0.0.1",9999)){
        try {
            socket = new Socket(hostname, port);
            input = this.socket.getInputStream();
            inputStreamReader=new InputStreamReader(input);
            bufferedReader = new BufferedReader(inputStreamReader);
            output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
            msg = bufferedReader.readLine();
            System.out.println(msg + " - to wiadomosc odebrana z socketa");

        } catch (UnknownHostException ex) {
            System.out.println("Serwer nieosiągalny: " + ex.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage(String msg) throws IOException {
        output = socket.getOutputStream();
        //writer=new PrintWriter(output,true);
        writer.println(msg);
        //writer.flush();
        // writer.close();
    }

    public void getMessage() {
        try {
            //  input= this.socket.getInputStream();
            // reader = new BufferedReader(new InputStreamReader(input));
            msg = bufferedReader.readLine();
            System.out.println(msg + " - to wyslana wiadomosc");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
