package com.miloszmomot.messenger;

import java.net.ServerSocket;

public abstract class Subject extends Thread {
    public abstract void addObserver(ServerSocket serverSocket);

    public abstract void removeObserver(ServerClientThread serverClientThread);

    public abstract void notifyObservers(byte[] string);
}
