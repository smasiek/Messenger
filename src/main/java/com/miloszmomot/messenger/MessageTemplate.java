package com.miloszmomot.messenger;

public class MessageTemplate {
    private byte preambula;
    private byte[] bytes;

    public MessageTemplate(byte[] receivedBytes) {
        preambula = receivedBytes[0];
        bytes = new byte[receivedBytes.length - 1];
        for (int i = 1; i < receivedBytes.length; i++) {
            bytes[i - 1] = receivedBytes[i];
        }
    }

    public byte getPreambula() {
        return preambula;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
