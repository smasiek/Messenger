package com.miloszmomot.messenger;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.*;

public class MessagesHandler extends JFrame implements MouseListener {
    private final Object message;
    //Download Image dialog elements
    private JDialog dDecisionImage = new JDialog();
    private JTextPane tPathPromptImage = new JTextPane();
    private TextAreaWithPrompt tPathImage = new TextAreaWithPrompt();
    private JButton bOkDecisionImage = new JButton();
    private JButton bNoImage = new JButton();
    //Download Audio dialog elements
    private JDialog dDecisionAudio = new JDialog();
    private JTextPane tPathPromptAudio = new JTextPane();
    private TextAreaWithPrompt tPathAudio = new TextAreaWithPrompt();
    private JButton bOkDecisionAudio = new JButton();
    private JButton bNoAudio = new JButton();

    //Formats needed to deserialize audio
    private AudioFormat audioFormat = null;
    private AudioFileFormat.Type audioFileFormat = null;

    public MessagesHandler(Object message) {
        this.message = message;
    }

    public void handleText(JTextArea textArea) {
        if (message instanceof String) {
            textArea.append("\n" + message);
        }
    }

    public void handleImage(int imageSize) {

        if (message instanceof BufferedImage) {

            // Download file dialog settings
            dDecisionImage.setBounds(200, 200, 400, 200);
            GridLayout gridDecision = new GridLayout(3, 1);

            GridLayout secondRow = new GridLayout(1, 2);
            dDecisionImage.setLayout(gridDecision);
            tPathPromptImage.setText("Someone sent an image\n\tImage size: " + (imageSize + 1) + "KB Should we download it?");
            setDialog(secondRow, tPathPromptImage, dDecisionImage, tPathImage, bOkDecisionImage, bNoImage);
            dDecisionImage.setVisible(true);

        }
    }


    public void handleAudio(Object handledMessage, AudioFormat audioFormat, AudioFileFormat.Type audioFileFormat) {

        if (message instanceof byte[]) {

            dDecisionAudio.setBounds(200, 200, 400, 200);
            GridLayout gridDecision = new GridLayout(3, 1);

            GridLayout secondRow = new GridLayout(1, 2);
            dDecisionAudio.setLayout(gridDecision);
            tPathPromptAudio.setText("Someone sent an audio file\n\tFile size: " + ((((byte[]) handledMessage).length / 1024) + 1) + "KB Should we download it?");
            setDialog(secondRow, tPathPromptAudio, dDecisionAudio, tPathAudio, bOkDecisionAudio, bNoAudio);
            this.audioFormat = audioFormat;
            this.audioFileFormat = audioFileFormat;
            dDecisionAudio.setVisible(true);
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

    private void setDialog(GridLayout secondRow, JTextPane tPathPromptImage, JDialog dDecisionImage, TextAreaWithPrompt tPathImage, JButton bOkDecisionImage, JButton bNoImage) {
        SimpleAttributeSet attribsDecision = new SimpleAttributeSet();
        StyleConstants.setAlignment(attribsDecision, StyleConstants.ALIGN_CENTER);
        StyleConstants.setSpaceAbove(attribsDecision, 5);
        tPathPromptImage.setParagraphAttributes(attribsDecision, true);
        tPathPromptImage.setEditable(false);
        dDecisionImage.add(tPathPromptImage);

        tPathImage.setLineWrap(true);
        dDecisionImage.add(tPathImage);

        JPanel buttons = new JPanel();
        buttons.setLayout(secondRow);
        bOkDecisionImage.setText("Download!");
        bNoImage.setText("No");
        bOkDecisionImage.addMouseListener(this);
        bNoImage.addMouseListener(this);
        buttons.add(bOkDecisionImage);
        buttons.add(bNoImage);


        dDecisionImage.add(buttons);
        dDecisionImage.setVisible(false);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Object p = e.getSource();
        if (p == bOkDecisionImage) {
            File outputfile = new File(tPathImage.getText());
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

            dDecisionImage.setVisible(false);

        } else if (p == bOkDecisionAudio) {
            File outputFile = new File(tPathAudio.getText());
            try {
                if (outputFile.createNewFile()) {

                    ByteArrayInputStream bais = new ByteArrayInputStream((byte[]) message);
                    AudioInputStream outputAIS = new AudioInputStream(bais, this.audioFormat,
                            ((byte[]) message).length);

                    AudioSystem.write(outputAIS, AudioFileFormat.Type.WAVE, outputFile);

                    System.out.println("File downloaded!");
                } else {
                    ByteArrayInputStream bais = new ByteArrayInputStream((byte[]) message);
                    AudioInputStream outputAIS = new AudioInputStream(bais, this.audioFormat,
                            ((byte[]) message).length);
                    AudioSystem.write(outputAIS, audioFileFormat, outputFile);
                    System.out.println("Downloaded file overidden another file.");
                }

                dDecisionAudio.setVisible(false);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (p == bNoImage) {
            dDecisionImage.setVisible(false);
            System.out.println("File omitted");
        } else if (p == bNoAudio) {
            dDecisionAudio.setVisible(false);
            System.out.println("File omitted");
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
