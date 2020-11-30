package com.miloszmomot.messenger;

import javafx.scene.Parent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.Scanner;

public class MessagesHandler {
    private final Object message;

    public MessagesHandler(Object message) {
        this.message = message;
    }

    public void handleText() {
        if (message instanceof String) {
            System.out.println((String) message);
        }
    }

    public void handleImage(String decision, String downloadPath) {

        if (decision.equals("y")) {
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
}
