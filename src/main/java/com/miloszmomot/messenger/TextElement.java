package com.miloszmomot.messenger;

import java.nio.charset.StandardCharsets;

public class TextElement extends ChainElement {
    public TextElement(byte datatype) {
        super(datatype);
    }

    @Override
    public String handleConcreteRequest(MessageTemplate messageTemplate) {
            return new String(messageTemplate.getBytes(), StandardCharsets.UTF_8);
    }
}
