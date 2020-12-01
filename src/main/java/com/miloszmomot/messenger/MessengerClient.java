package com.miloszmomot.messenger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class MessengerClient extends JFrame implements Chatable, MouseListener {

    private boolean welcomed = false;
    private DataInputStream input;
    private BufferedReader bufferedReader;
    private String msg;
    private Socket socket;
    private DataOutputStream output;
    private Scanner scanner;
    private String nickname;

    private InputStreamReader inputStreamReader;
    //private PrintWriter writer;

    private Thread sendMessagesThread;
    private Thread receiveMessagesThread;
    static boolean runSendThread = true;
    static boolean runReadThread = true;

    private String downloadPath = null;
    private String decision = null;
    private boolean imageDownloading = false;

    private JTextArea tMessages = new JTextArea();
    private TextAreaWithPrompt tNewMessage = new TextAreaWithPrompt();
    private JButton bSendImage = new JButton("Send image");
    private JButton bSendMusic = new JButton("Send music");
    private JButton bSend = new JButton("Send");


    private JDialog errorDialog = new JDialog();
    private JTextPane tError = new JTextPane();
    private JButton bOkError = new JButton("Ok");


    public MessengerClient() {
        setSize(400, 525);
        setTitle("Messenger");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //getContentPane().setBackground(Color.white);

        setLayout(null);
        setResizable(false);

        tMessages.setBounds(0, 0, 400, 400);
        tMessages.setEditable(false);
        //tMessages.setText();
        add(tMessages);

        tNewMessage.setBounds(0, 400, 395, 50);
        //tNewMessage.setAlignmentX(JFrame.LEFT_ALIGNMENT);
        //tNewMessage.setText("przykładowa wiadomość");
        tNewMessage.setLineWrap(true);
        add(tNewMessage);

        bSendMusic.setBounds(0, 450, 133, 50);
        bSendMusic.addMouseListener(this);
        add(bSendMusic);

        bSendImage.setBounds(133, 450, 133, 50);
        bSendImage.addMouseListener(this);
        add(bSendImage);

        bSend.setBounds(266, 450, 133, 50);
        bSend.addMouseListener(this);
        add(bSend);


        // Wrong path dialog settings
        errorDialog.setBounds(200, 200, 200, 200);
        GridLayout grid = new GridLayout();
        grid.setColumns(1);
        grid.setRows(2);
        errorDialog.setLayout(grid);
        //tError.setBounds(0,0,200,100);
        SimpleAttributeSet attribs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_CENTER);
        StyleConstants.setSpaceAbove(attribs, 30);
        tError.setParagraphAttributes(attribs, true);
        tError.setEditable(false);
        errorDialog.add(tError);

        bOkError.addMouseListener(this);

        errorDialog.add(bOkError);
        errorDialog.setVisible(false);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int i = JOptionPane.showConfirmDialog(null, "Do you want to leave?");
                if (i == 0)
                    System.out.println("Closing this connection : " + socket);
                Integer premabula = -1;
                try {
                    output.write(prepareBytes("exit".getBytes(), premabula.byteValue()));
                } catch (IOException ex) {
                    ex.printStackTrace();
                    System.exit(0);
                }
                //output.writeUTF(msg);
                System.out.println("Connection closed");
                runReadThread = false;

                System.exit(0);

            }
        });

        setVisible(true);
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        Object p = e.getSource();
        if (p == bSend) {
            try {
                String messageToSend = nickname+": "+tNewMessage.getText();
                if (!messageToSend.equals(nickname+": "))
                    if (messageToSend.equals("exit")) {
                        System.out.println("Closing this connection : " + socket);
                        Integer premabula = -1;
                        output.write(prepareBytes(messageToSend.getBytes(), premabula.byteValue()));
                        //output.writeUTF(msg);
                        scanner.close();
                        System.out.println("Connection closed");
                        runReadThread = false;
                    }
                    // write on the output stream
                    else {
                        Integer premabula = 0;
                        output.write(prepareBytes(messageToSend.getBytes(), premabula.byteValue()));
                    }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            tNewMessage.setText("");
        } else if (p == bSendImage) {
            try {
                //System.out.println("debuggfin");
                String path = tNewMessage.getText();
                //System.out.println("Podaj sciezke zdjecia:");
                // String path = scanner.nextLine();
                if (!path.equals("")) {
                    File file = new File(path);
                    //Prepare byte array
                    BufferedImage img = ImageIO.read(file);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(img, "jpg", baos);
                    //Prepare bytes to send
                    Integer preambula = 1;

                    output.write(prepareBytes(baos.toByteArray(), preambula.byteValue()));
                } else {
                    tError.setText("Podano złą ścieżke");
                    errorDialog.setVisible(true);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            tNewMessage.setText("");
        } else if (p == bOkError) {
            errorDialog.setVisible(false);
            tNewMessage.setText("");
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public class TextAreaWithPrompt extends JTextArea {

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);

            if (getText().isEmpty() && !(FocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == this)) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setBackground(Color.gray);
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                g2.drawString("Wpisz wiadomosc lub scieżke pliku", 5, 10); //figure out x, y from font's FontMetrics and size of component.
                g2.dispose();
            }
        }

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
            //sendMessages();


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
                                    //sleep(30000);

                                } else {
                                    Integer premabula = 0;
                                    output.write(prepareBytes(msg.getBytes(), premabula.byteValue()));
                                }

                            } catch (IOException e) {
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
                        if (!welcomed) {
                            nickname=input.readUTF();
                            welcomed=true;
                        }else{
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
                                messagesHandler.handleText(tMessages);
                            } else if (handledMessage instanceof BufferedImage) {
                                double imageSize = message.getBytes().length / 1024.0;
                                messagesHandler.handleImage(imageSize);
                            }

                            messageArray.clear();
                        }
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