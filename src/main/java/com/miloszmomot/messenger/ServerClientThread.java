package com.miloszmomot.messenger;

import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;

public class ServerClientThread extends Thread{
    private final Socket socket;
    private final DataInputStream input;
    private final BufferedReader reader;
    private final DataOutputStream output;
    private final PrintWriter writer;

    private final Server server;

    public ServerClientThread(Socket socket, Server server) throws IOException {
        this.socket =socket;
        input= new DataInputStream(this.socket.getInputStream());
        reader= new BufferedReader(new InputStreamReader(input));
        output= new DataOutputStream(socket.getOutputStream());
        writer=new PrintWriter(output,true);

        this.server=server;

    }

    public void run(){
        String received;
        String toReturn;

        //TODO To o wpisaniu exit potem usunąć i wysyłanie wiadomości "exit" będzie odbywało sie
        // po kliknieciu przycisku "exit chat"
        while(true) {
            try {
                writer.println("You've joined the chat. Start typing\n" +
                        "Type \"exit\" to leave chat");
                received = input.readUTF();

                if(!received.equals("")) {
                    if (received.equals("Exit")) {
                        System.out.println("Client " + this.socket + " sends exit...");
                        System.out.println("Closing this connection.");
                        this.socket.close();
                        System.out.println("Connection closed");
                        break;
                    }

                    //Rozeslanie obserwujacym wiadomosci
                    server.notifyObservers(received);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void messagePropagation(String string) {
        writer.println(string);
    }

    /*    public void newText(){
        writer.println("nowy tekst.");
        writer.flush();
        writer.close();

    }*/



}
