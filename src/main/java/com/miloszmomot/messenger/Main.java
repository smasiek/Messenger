package com.miloszmomot.messenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

        ServerThread serverThread;

        try (ServerSocket serverSocket = new ServerSocket(9999)) {
            while (true) {
                Socket socket = serverSocket.accept();
                //Po moich zmianach:
                serverThread = new ServerThread(socket);
                serverThread.start(); //przykładowa obsługa żądania
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try (Socket socket = new Socket("hostname",9999)){
            InputStream input= socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String msg=reader.readLine();
            System.out.println(msg);
        }catch (UnknownHostException ex){
            System.out.println("Serwer nieosiągalny: " + ex.getMessage());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("kupa");



    }


}
