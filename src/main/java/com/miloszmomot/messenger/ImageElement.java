package com.miloszmomot.messenger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ImageElement extends ChainElement {
    public ImageElement(byte dataType) {
        super(dataType);
    }

    @Override
    public BufferedImage handleConcreteRequest(MessageTemplate messageTemplate) {

        BufferedImage bImage = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(messageTemplate.getBytes());
            bImage = ImageIO.read(bis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bImage;
    }
}
