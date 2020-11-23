package com.miloszmomot.messenger;

public interface Observer {

    //TODO: zmienic string na messageTempalte
    void update(String wiadomosc);
    void send(String wiadomosc);

}
