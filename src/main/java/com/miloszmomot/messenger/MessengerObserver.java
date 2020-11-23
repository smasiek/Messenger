package com.miloszmomot.messenger;

public class MessengerObserver implements Observer {

    //TODO string zamienic na TempalateMethod chyba
    private String text;
    private Subject server;

    public MessengerObserver(Subject server){
        text="Pusty tekst";
        this.server=server;
        server.addObserver(this);
    }

    public void update(String wiadomosc) {
        text=wiadomosc;
    }

    public void send(String wiadomosc) {
        server.notifyObservers(wiadomosc);
    }

    public void getText(){
        System.out.println(text);
    }
}
