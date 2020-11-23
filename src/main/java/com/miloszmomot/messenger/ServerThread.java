package com.miloszmomot.messenger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static sun.plugin2.main.client.LiveConnectSupport.shutdown;

public class ServerThread extends Thread{
    private final Socket socket;
    private InputStream input;
    private BufferedReader reader;
    private OutputStream output;
    private PrintWriter writer;

    public ServerThread(Socket socket) throws IOException {
        this.socket =socket;
        input=this.socket.getInputStream();
        reader= new BufferedReader(new InputStreamReader(input));
        output=socket.getOutputStream();
        writer=new PrintWriter(output,true);

    }

    public void run(){
        writer.println("Watek serwera wystartowal.");
        writer.flush();
        writer.close();

    }

/*    public void newText(){
        writer.println("nowy tekst.");
        writer.flush();
        writer.close();

    }*/



}
