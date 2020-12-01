package com.miloszmomot.messenger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class MessagesHandler extends JFrame implements MouseListener {
    private final Object message;

    private JDialog dDecision = new JDialog();
    private JTextPane tPathPrompt = new JTextPane();
    private TextAreaWithPrompt tPath = new TextAreaWithPrompt();
    private JButton bOkDecision = new JButton();
    private JButton bNo = new JButton();

    public MessagesHandler(Object message) {
        this.message = message;
    }

    public void handleText(JTextArea textArea) {
        if (message instanceof String) {
            //System.out.println((String) message);
            String chatString = textArea.getText();
            textArea.setText(chatString + "\n" + (String) message);
        }
    }

    // public void handleImage(String decision, String downloadPath) {
    public void handleImage(double imageSize) {

        /*if (decision.equals("y")) {
            File outputfile = new File(downloadPath);
            try {
                if (outputfile.createNewFile()) {
                    ImageIO.write((BufferedImage) message, "jpg", outputfile);

                    System.out.println("File downloaded!");
                } else {
                    try {
                        ImageIO.write((BufferedImage) message, "jpg", outputfile);
                        System.out.println("Downloaded file overidden another file.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File ommited");
        }*/

        if (message instanceof BufferedImage) {



            // Download file dialog settings
            dDecision.setBounds(200, 200, 400, 200);
            GridLayout gridDecision = new GridLayout(3, 1);

            GridLayout secondRow = new GridLayout(1, 2);
            dDecision.setLayout(gridDecision);
            //tError.setBounds(0,0,200,100);
            tPathPrompt.setText("Someone sent an image\n\tImage size: " + ((int)imageSize+1) + "KB Should we download it?");
            SimpleAttributeSet attribsDecision = new SimpleAttributeSet();
            StyleConstants.setAlignment(attribsDecision, StyleConstants.ALIGN_CENTER);
            StyleConstants.setSpaceAbove(attribsDecision, 5);
            tPathPrompt.setParagraphAttributes(attribsDecision, true);
            tPathPrompt.setEditable(false);
            dDecision.add(tPathPrompt);

            tPath.setLineWrap(true);
            dDecision.add(tPath);

            JPanel buttons = new JPanel();
            buttons.setLayout(secondRow);
            bOkDecision.setText("Download!");
            bNo.setText("No");
            bOkDecision.addMouseListener(this);
            bNo.addMouseListener(this);
            buttons.add(bOkDecision);
            buttons.add(bNo);


            dDecision.add(buttons);
            dDecision.setVisible(false);

            dDecision.setVisible(true);

        }
    }

    public class TextAreaWithPrompt extends JTextArea {

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);

            if (getText().isEmpty() && !(FocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == this)) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setBackground(Color.gray);
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                g2.drawString("Type download path!\n path\\\\file_name.jpg", 5, 10); //figure out x, y from font's FontMetrics and size of component.
                g2.dispose();
            }
        }

    }

    public void handleMessage() {
        Scanner scanner = new Scanner(System.in);
        scanner.reset();
        if (message instanceof String) {
            System.out.println((String) message);
        } else if (message instanceof BufferedImage) {
            System.out.println("Someone sent image!\n\tDo you want to download it? y/n");

            String decision = scanner.nextLine();
            //    String decision=client.getDecision();
            if (decision.equals("y")) {
                System.out.println("Type download destination (path\\file_name.jpg)");
                String downloadPath = scanner.nextLine();
                //  String downloadPath=client.getPath();

                //File outputfile = new File("C:\\Users\\smasi\\OneDrive\\Dokumenty\\GitHub\\Messenger\\src\\main\\resources\\zdjeciePobrane.jpg");

                File outputfile = new File(downloadPath);

                try {
                    if (outputfile.createNewFile()) {
                        ImageIO.write((BufferedImage) message, "jpg", outputfile);

                        System.out.println("File downloaded!");
                    } else {
                        try {
                            ImageIO.write((BufferedImage) message, "jpg", outputfile);
                            System.out.println("Downloaded file overidden another file.");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("File ommited");
            }
                /*if(outputfile.exists()) {
                    try {
                        ImageIO.write((BufferedImage) message, "jpg", outputfile);
                        System.out.println("Downloaded file overidden another file.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    //String regex="\\\\";
                    //String[] pathWithoutBackslashes=downloadPath.split(regex);
                    try {
                        if(outputfile.createNewFile()){
                            try {
                                ImageIO.write((BufferedImage) message, "jpg", outputfile);
                                System.out.println("Downloaded file overidden another file.");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println("File downloaded!");
                        } else {
                            System.out.println("Error while downloading file");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }*/
            //}
            // } else if (message instanceof AudioElement){
            //rob cos tam
            scanner.close();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Object p = e.getSource();
        if (p == bOkDecision) {
            File outputfile = new File(tPath.getText());
            try {
                if (outputfile.createNewFile()) {
                    ImageIO.write((BufferedImage) message, "jpg", outputfile);

                    System.out.println("File downloaded!");
                } else {

                    ImageIO.write((BufferedImage) message, "jpg", outputfile);
                    System.out.println("Downloaded file overidden another file.");

                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            dDecision.setVisible(false);
        } else if (p == bNo) {
            dDecision.setVisible(false);
            System.out.println("File ommited");
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
