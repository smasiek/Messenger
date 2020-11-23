package com.miloszmomot.messenger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) {
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
        try (Socket socket = new Socket("127.0.0.1",9999)){
            InputStream input= socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String msg=reader.readLine();
            System.out.println(msg + " test");
            server.komenda();
            msg=reader.readLine();
            System.out.println(msg + " test");

        }catch (UnknownHostException ex){
            System.out.println("Serwer nieosiągalny: " + ex.getMessage());
        }
        catch (IOException e) {
            e.printStackTrace();
        }




    }


}
