package com.miloszmomot.messenger;

import javafx.scene.Parent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class MessagesHandler {
    public void handleMessage(Object message, Scanner scanner){
        if (message instanceof String){
            System.out.println((String)message);
        } else if(message instanceof BufferedImage){
            System.out.println("Someone sent image!\n\tDo you want to download it? y/n");
            String decision=scanner.nextLine();
            if(decision.equals("y")){
                System.out.println("Type download destination (path\\file_name.jpg)");
                String downloadPath=scanner.nextLine();
                File outputfile = new File(downloadPath);
                if(outputfile.exists()) {
                    try {
                        ImageIO.write((BufferedImage) message, "png", outputfile);
                        System.out.println("Downloaded file overidden another file.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    //String regex="\\\\";
                    //String[] pathWithoutBackslashes=downloadPath.split(regex);
                    try {
                        if(outputfile.createNewFile()){
                            System.out.println("File downloaded!");
                        } else {
                            System.out.println("Error while downloading file");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (message instanceof AudioElement){
            //rob cos tam
        }
    }
}
