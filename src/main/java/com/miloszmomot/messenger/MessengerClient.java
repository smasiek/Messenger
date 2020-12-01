package com.miloszmomot.messenger;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
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

public class MessengerClient extends JFrame implements Chatable, MouseListener {

    private boolean welcomed = false;
    private DataInputStream input;
    private DataOutputStream output;
    private Socket socket;
    private String nickname;

    static boolean runReadThread = true;

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

        setLayout(null);
        setResizable(false);

        tMessages.setSize(390, 400);
        tMessages.setEditable(false);
        tMessages.setLineWrap(true);
        tMessages.setVisible(true);

        JScrollPane scroll = new JScrollPane(tMessages);
        scroll.setSize(390, 400);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVisible(true);
        this.add(scroll);


        tNewMessage.setBounds(0, 400, 395, 50);
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
                int premabula = -1;
                try {
                    output.write(prepareBytes("exit".getBytes(), (byte) premabula));
                } catch (IOException ex) {
                    ex.printStackTrace();
                    System.exit(0);
                }
                System.out.println("Connection closed");
                runReadThread = false;
                System.exit(0);

            }
        });

        setVisible(true);
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
            InetAddress ip = InetAddress.getByName("localhost");
            socket = new Socket(ip, port);
            input = new DataInputStream(this.socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            receiveMessages();
        } catch (UnknownHostException ex) {
            System.out.println("Server unavailable: " + ex.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] prepareBytes(byte[] byteArray, byte preambula) {
        byte[] bytesToSend = new byte[byteArray.length + 1];
        bytesToSend[0] = preambula;
        System.arraycopy(byteArray, 0, bytesToSend, 1, byteArray.length);
        return bytesToSend;
    }

    public void receiveMessages() {
        Thread receiveMessagesThread = new Thread(new Runnable() {

            @Override
            public void run() {
                ChainElement textElement = new TextElement((byte) 0);
                ChainElement imageElement = new ImageElement((byte) 1);
                ChainElement audioElement = new AudioElement((byte) 2);
                ChainElement audioFormatElement = new AudioFormatElement((byte) 3);
                ChainElement audioFileTypeElement = new AudioFileTypeElement((byte) 4);

                textElement.addNextElement(imageElement);
                imageElement.addNextElement(audioElement);
                audioElement.addNextElement(audioFormatElement);
                audioFormatElement.addNextElement(audioFileTypeElement);

                AudioFormat audioFormat = null;
                AudioFileFormat.Type audioFileFormat = null;
                while (runReadThread) {
                    try {

                        // read the message sent to this client
                        byte[] receivedMessage;
                        int messageSize = 0;
                        if (!welcomed) {
                            nickname = input.readUTF();
                            welcomed = true;
                        } else {
                            ArrayList<Byte> messageArray = new ArrayList<>();

                            while (input.available() != 0) {
                                //make sure to get full inputStream instead of half of it
                                messageSize = input.available();
                                receivedMessage = new byte[messageSize];
                                input.read(receivedMessage);
                                for (int i = 0; i < messageSize; i++) {
                                    messageArray.add(receivedMessage[i]);
                                }
                            }

                            if (messageSize != 0) {

                                receivedMessage = new byte[messageArray.size()];
                                for (int i = 0; i < messageArray.size(); i++) {
                                    receivedMessage[i] = messageArray.get(i);
                                }

                                MessageTemplate message = new MessageTemplate(receivedMessage);

                                Object handledMessage = textElement.handleRequest(message);
                                MessagesHandler messagesHandler = new MessagesHandler(handledMessage);

                                if (handledMessage instanceof String) {
                                    messagesHandler.handleText(tMessages);
                                } else if (handledMessage instanceof BufferedImage) {
                                    int imageSize = message.getBytes().length / 1024;
                                    messagesHandler.handleImage(imageSize);
                                } else if (handledMessage instanceof AudioFileFormat.Type) {
                                    audioFileFormat = (AudioFileFormat.Type) handledMessage;
                                } else if (handledMessage instanceof AudioFormat) {
                                    audioFormat = (AudioFormat) handledMessage;
                                } else if (handledMessage instanceof byte[]) {

                                    messagesHandler.handleAudio(handledMessage, audioFormat, audioFileFormat);
                                    audioFormat = null;
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

    @Override
    public void mouseClicked(MouseEvent e) {
        Object p = e.getSource();
        if (p == bSend) {
            try {
                String messageToSend = nickname + ": " + tNewMessage.getText();
                if (!messageToSend.equals(nickname + ": "))
                    if (messageToSend.equals("exit")) {
                        System.out.println("Closing this connection : " + socket);
                        int premabula = -1;
                        output.write(prepareBytes(messageToSend.getBytes(), (byte) premabula));
                        System.out.println("Connection closed");
                        runReadThread = false;
                    }
                    // write on the output stream
                    else {
                        int premabula = 0;
                        output.write(prepareBytes(messageToSend.getBytes(), (byte) premabula));
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
                    int preambula = 1;

                    output.write(prepareBytes(baos.toByteArray(), (byte) preambula));
                } else {
                    tError.setText("Podano złą ścieżke");
                    errorDialog.setVisible(true);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            tNewMessage.setText("");
        } else if (p == bSendMusic) {
            try {
                //System.out.println("debuggfin");
                String path = tNewMessage.getText();
                //System.out.println("Podaj sciezke zdjecia:");
                // String path = scanner.nextLine();
                if (!path.equals("")) {

                    File file = new File(path);
                    //Prepare byte array
                    InputStream in = AudioSystem.getAudioInputStream(file);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
                    AudioFileFormat.Type targetFileType = fileFormat.getType();
                    AudioFormat audioFormat = fileFormat.getFormat();

                    String[] tempAudioFormatArray = audioFormat.toString().split(" ");
                    boolean bigEndian = tempAudioFormatArray[tempAudioFormatArray.length - 1].equals("big-endian");

                    String audioFormatString = audioFormat.getEncoding().toString() + " " +
                            audioFormat.getSampleRate() + " " +
                            audioFormat.getSampleSizeInBits() + " " +
                            audioFormat.getChannels() + " " +
                            audioFormat.getFrameSize() + " " +
                            audioFormat.getFrameRate() + " " +
                            bigEndian;


                    int preambula = 3;
                    output.write(prepareBytes(audioFormatString.getBytes(), (byte) preambula));

                    String audioFileFormatTypeString = targetFileType.toString();

                    preambula = 4;
                    output.write(prepareBytes(audioFileFormatTypeString.getBytes(), (byte) preambula));

                    int read;
                    byte[] buff = new byte[1024];
                    while ((read = in.read(buff)) > 0) {
                        baos.write(buff, 0, read);
                    }
                    baos.flush();
                    byte[] audioBytes = baos.toByteArray();


                    //Prepare bytes to send
                    preambula = 2;

                    output.write(prepareBytes(audioBytes, (byte) preambula));
                } else {
                    tError.setText("Podano złą ścieżke");
                    errorDialog.setVisible(true);
                }
            } catch (IOException | UnsupportedAudioFileException ex) {
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
}
