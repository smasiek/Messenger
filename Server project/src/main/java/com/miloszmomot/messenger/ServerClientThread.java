package com.miloszmomot.messenger;

import java.io.*;
import java.util.*;
import java.net.*;

public class ServerClientThread extends Thread {
    private final Socket socket;
    private final DataInputStream input;
    private final DataOutputStream output;

    private final Server server;

    private boolean welcomeMessageShown = false;

    public ServerClientThread(Socket socket, Server server) throws IOException {
        this.socket = socket;
        input = new DataInputStream(this.socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
        this.server = server;
    }

    public void run() {
        byte[] received;
        int messageSize;
        ArrayList<Byte> message = new ArrayList<>();
        String nickname = "User " + server.getObserverIndex(this);
        while (true) {
            messageSize = 0;
            try {
                if (!welcomeMessageShown) {
                    output.writeUTF(nickname);
                    output.writeUTF("You've joined the chat!\n" +
                            "Your nickname is: " + nickname + "\n" +
                            "To send an image type path and click proper button\n" +
                            "To send a music track follow the same routine\n" +
                            "-----------------------------------------------------------------------");
                    welcomeMessageShown = true;
                }

                while (input.available() != 0) {

                    messageSize = input.available();
                    received = new byte[messageSize];
                    input.read(received);
                    for (int i = 0; i < messageSize; i++) {
                        message.add(received[i]);
                    }
                }

                if (messageSize != 0) {
                    received = new byte[message.size()];
                    for (int i = 0; i < message.size(); i++) {
                        received[i] = message.get(i);
                    }
                    if (received[0] == -1) {
                        System.out.println("Client " + this.socket + " sends exit...");
                        System.out.println("Closing this connection.");
                        this.socket.close();
                        System.out.println("Connection closed");
                        server.removeObserver(this);
                        break;
                    } else if (isTypeOfMessageKnown(new byte[]{0, 1, 2, 3, 4}, received[0])) {
                        server.notifyObservers(received);
                    } else {
                        throw new IOException("Unknown type of message");
                    }
                    message.clear();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            // closing resources
            this.input.close();
            this.output.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean isTypeOfMessageKnown(byte[] types, byte messageType) {
        for (byte type : types) {
            if (type == messageType) return true;
        }
        return false;
    }

    public void messagePropagation(byte[] message) {
        try {
            output.write(message);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
