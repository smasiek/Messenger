package com.miloszmomot.messenger;

public class Main {

    public static void main(String[] args){

        MessengerClient observer = new MessengerClient();

        int port=5657;
        observer.connectToSocket(port);

    }
}
