package com.miloszmomot.messenger;

public abstract class ChainElement {
    protected byte dataType;
    protected ChainElement nextElement;

    public ChainElement(byte dataType) {
        this.dataType = dataType;
    }

    public void addNextElement(ChainElement nextElement) {
        this.nextElement = nextElement;
    }

    public Object handleRequest(MessageTemplate messageTemplate) {
        if (checkDataType(messageTemplate.getPreambula())) {
            return handleConcreteRequest(messageTemplate);
        } else {
            return nextElement.handleRequest(messageTemplate);
        }
    }

    public abstract Object handleConcreteRequest(MessageTemplate messageTemplate);

    public boolean checkDataType(byte dataType) {
        //return true;
        return this.dataType == dataType;
    }

}
