package com.miloszmomot.messenger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Thread.sleep;

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
    static boolean runSendThread = true;
    static boolean runReadThread = true;

    private String downloadPath = null;
    private String decision = null;
    private boolean imageDownloading = false;


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
                while (runSendThread) {
                    // read the message to deliver.
                    //while(!scanner.hasNext());
                    if (!imageDownloading) {


                        if (scanner.hasNext()) {
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
                                    runReadThread = false;
                                }
                                // write on the output stream
                                if (msg.equals("zdjecie")) {
                                    //Inform server about type of message (nie potrzebne bo chainmethod to zrobi
                                    //output.writeUTF(msg);

                                    //File path set
                                    System.out.println("Podaj sciezke zdjecia:");
                                    String path = scanner.nextLine();
                                    File file = new File(path);
                                    //Prepare byte array
                                    BufferedImage img = ImageIO.read(file);
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    ImageIO.write(img, "jpg", baos);
                                    //Prepare bytes to send
                                    Integer preambula = 1;

                                    output.write(prepareBytes(baos.toByteArray(), preambula.byteValue()));
                                    sleep(1000);

                                } else {
                                    Integer premabula = 0;
                                    output.write(prepareBytes(msg.getBytes(), premabula.byteValue()));
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
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
                ChainElement textElement = new TextElement((byte) 0);
                ChainElement imageElement = new ImageElement((byte) 1);
                textElement.addNextElement(imageElement);
                while (runReadThread) {
                    try {

                        // read the message sent to this client
                        byte[] receivedMessage = null;
                        int messageSize = 0;
                        ArrayList<Byte> messageArray = new ArrayList<>();
                        while (input.available() != 0) {

                            messageSize = input.available();
                            //messageSize = 352403;
                            receivedMessage = new byte[messageSize];
                            input.read(receivedMessage);
                            for (int i = 0; i < messageSize; i++) {
                                messageArray.add(receivedMessage[i]);
                            }
                        }


                        //messageSize = input.available();
                        //receivedMessage = new byte[messageSize];
                        //input.read(receivedMessage);
                        if (messageSize != 0) {
                            receivedMessage = new byte[messageArray.size()];
                            for (int i = 0; i < messageArray.size(); i++) {
                                receivedMessage[i] = messageArray.get(i);
                            }
                            MessageTemplate message = new MessageTemplate(receivedMessage);


                            Object handledMessage = textElement.handleRequest(message);
                            //scanner.wait();
                            MessagesHandler messagesHandler = new MessagesHandler(handledMessage);

                            if (handledMessage instanceof String) {
                                messagesHandler.handleText();
                            } else if (handledMessage instanceof BufferedImage) {
                                runSendThread=false;
                                imageDownloading = true;

                                System.out.println("Someone sent image!\n\tDo you want to download it? y/n");
                                decision = scanner.nextLine();
                                //String downloadPath=null;
                                //    String decision=client.getDecision();
                                if (decision.equals("y")) {
                                    System.out.println("Type download destination (path\\file_name.jpg)");
                                    downloadPath = scanner.nextLine();
                                }
                                messagesHandler.handleImage(decision, downloadPath);

                                decision = null;
                                downloadPath = null;
                                imageDownloading = false;
                                runSendThread=true;
                                sendMessages();
                                System.out.println("dupa");
                            }


                            //scanner.close();
                            // sendMessagesThread.stop();
                            // scanner.wait();
                            //  scanner.close();
                            //messagesHandler.handleMessage(handledMessage);
                            //System.out.println(msg);
                            messageArray.clear();
                            // scanner.notify();
                            //  scanner=new Scanner(System.in);
                            // runSendThread =true;
                            // sendMessagesThread.notify();
                        }
                    } catch (EOFException e) {
                        System.out.println("Receiving thread is closed.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
        receiveMessagesThread.start();

    }

    public String getDecision() {
        scanner.reset();
        while (!scanner.hasNext()) ;
        String decision = scanner.nextLine();
        return decision;
    }

    public String getPath() {
        while (!scanner.hasNext()) ;
        String path = scanner.nextLine();
        return path;
    }

}
/*
C:\Users\smasi\OneDrive\Dokumenty\GitHub\Messenger\src\main\resources\zdjecie.jpg
 */