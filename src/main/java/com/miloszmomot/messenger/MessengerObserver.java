package com.miloszmomot.messenger;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class MessengerObserver implements Observer {

    //TODO string zamienic na TempalateMethod chyba
    private String text;
    private Subject server;

    private boolean welcomed=false;

    public MessengerObserver(Subject server) {
        text = "Pusty tekst";
        this.server = server;
        //server.addObserver(this);
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


    DataInputStream input;
    BufferedReader bufferedReader;
    String msg;
    Socket socket;
    DataOutputStream output;
    PrintWriter writer;
    InputStreamReader inputStreamReader;
    public void connectToSocket(int port) {

        //try (Socket socket = new Socket("127.0.0.1",9999)){
        try {

            Scanner scn=new Scanner(System.in);

            InetAddress ip= InetAddress.getByName("localhost");
            socket = new Socket(ip, port);

            input = new DataInputStream(this.socket.getInputStream());
            inputStreamReader=new InputStreamReader(input);
            bufferedReader = new BufferedReader(inputStreamReader);
            output = new DataOutputStream(socket.getOutputStream());
            writer = new PrintWriter(output, true);

            while (true){
                if(!welcomed) {
                    System.out.println(input.readUTF());
                    welcomed=true;
                }
                String toSend= scn.nextLine();
                output.writeUTF(toSend);
                if(toSend.equals("exit") )
                {
                    System.out.println("Closing this connection : " + socket);
                    socket.close();
                    System.out.println("Connection closed");
                    break;
                }

                String received = input.readUTF();
                System.out.println(received);
            }

            //msg = bufferedReader.readLine();
            System.out.println(msg);

            scn.close();
            input.close();
            output.close();

        } catch (UnknownHostException ex) {
            System.out.println("Serwer nieosiągalny: " + ex.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) throws IOException {
        //output = socket.getOutputStream();
        //writer=new PrintWriter(output,true);
        writer.println(msg);
        writer.flush();
        writer.close();
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
