package com.miloszmomot.messenger;

import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread{
    private Socket socket;
    private InputStream input;
    private BufferedReader reader;
    private OutputStream output;
    private PrintWriter writer;

    public ServerThread(Socket socket) throws IOException {
        this.socket =socket;
        this.input=socket.getInputStream();
        this.reader= new BufferedReader(new InputStreamReader(input));
        this.output=socket.getOutputStream();
        writer=new PrintWriter(output,true);
    }

    public void run(){
        try{
            writer.println("Wiadomosc.");
            socket.close();
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

}
