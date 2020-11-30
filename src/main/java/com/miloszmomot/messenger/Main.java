package com.miloszmomot.messenger;

import java.io.*;

import static java.lang.Thread.sleep;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        MessengerClient observer = new MessengerClient();

        int port=8080;
        observer.connectToSocket(port);
    }
}
