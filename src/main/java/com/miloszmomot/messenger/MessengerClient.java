package com.miloszmomot.messenger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class MessengerClient implements Chatable {

    private boolean welcomed = false;
    private DataInputStream input;
    private BufferedReader bufferedReader;
    private String msg;
    private Socket socket;
    private DataOutputStream output;
    private Scanner scanner;

    private InputStreamReader inputStreamReader;
    //private PrintWriter writer;

    private Thread sendMessagesThread;
    private Thread receiveMessagesThread;
    static boolean runThreads = true;


    public MessengerClient() {

    }


    public void connectToSocket(int port) {

        try {

            scanner = new Scanner(System.in);

            InetAddress ip = InetAddress.getByName("localhost");
            socket = new Socket(ip, port);

            input = new DataInputStream(this.socket.getInputStream());
            inputStreamReader = new InputStreamReader(input);
            bufferedReader = new BufferedReader(inputStreamReader);
            output = new DataOutputStream(socket.getOutputStream());
            //writer = new PrintWriter(output, true);

            receiveMessages();
            sendMessages();


        } catch (UnknownHostException ex) {
            System.out.println("Serwer nieosiągalny: " + ex.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessages() {
        //output = socket.getOutputStream();
        //writer=new PrintWriter(output,true);

        sendMessagesThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (runThreads) {
                    // read the message to deliver.
                    String msg = scanner.nextLine();
                    /*
                    byte[] msgInBytes=msg.getBytes();
                    byte[] msgInBytesToSend=prepareBytes(msgInBytes,);
                    */
                    try {
                        if (msg.equals("exit")) {
                            System.out.println("Closing this connection : " + socket);
                            Integer premabula = -1;
                            output.write(prepareBytes(msg.getBytes(), premabula.byteValue()));
                            //output.writeUTF(msg);
                            scanner.close();
                            System.out.println("Connection closed");
                            runThreads = false;
                        }
                        // write on the output stream
                        if (msg.equals("zdjecie")) {
                            //Inform server about type of message (nie potrzebne bo chainmethod to zrobi
                            //output.writeUTF(msg);

                            //File path set
                            String path = scanner.nextLine();
                            File file = new File(path);
                            //Prepare byte array
                            BufferedImage img = ImageIO.read(file);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ImageIO.write(img, "jpg", baos);
                            //Prepare bytes to send
                            Integer preambula = 1;

                            output.write(prepareBytes(baos.toByteArray(), preambula.byteValue()));

                        } else {
                            Integer premabula = 0;
                            output.write(prepareBytes(msg.getBytes(), premabula.byteValue()));
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        sendMessagesThread.start();
    }

    private byte[] prepareBytes(byte[] byteArray, byte preambula) {
        byte[] bytes = byteArray;
        byte[] bytesToSend = new byte[bytes.length + 1];
        bytesToSend[0] = preambula; //0 teskt, 1 zdjecie , 2 waveform
        for (int i = 1; i <= bytes.length; i++) {
            bytesToSend[i] = bytes[i - 1];
        }
        return bytesToSend;
    }


    public void receiveMessages() {
        //TODO to sie przyda do odbierania roznego rodzaju wiadomosci
        // poki co sa to stringi wysylane jako UTF8
        // ale soon to powinny być ciagi bitów z pierwszym bitem mowiacym
        // co to jest za plik
        // tutaj bedzie tez Wzorzec łancuch
        receiveMessagesThread = new Thread(new Runnable() {

            @Override
            public void run() {
                ChainElement textElement=new TextElement((byte) 0);
                ChainElement imageElement=new ImageElement((byte) 1);
                imageElement.addNextElement(textElement);
                while (runThreads) {
                    try {

                        // read the message sent to this client
                        byte[] receivedMessage;
                        int messageSize;
                        messageSize = input.available();
                        receivedMessage = new byte[messageSize];
                        input.read(receivedMessage);
                        if (messageSize != 0) {

                            MessageTemplate message = new MessageTemplate(receivedMessage);
                            Object handledMessage=imageElement.handleRequest(message);
                            MessagesHandler messagesHandler=new MessagesHandler();
                            messagesHandler.handleMessage(handledMessage,scanner);
                            //System.out.println(msg);
                        }
                    } catch (EOFException e){
                        System.out.println("Receiving thread is closed.");
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        receiveMessagesThread.start();

    }
}
