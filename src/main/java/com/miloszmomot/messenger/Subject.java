package com.miloszmomot.messenger;

public interface Subject {
     void addObserver(Observer observer);
     void removeObserver(Observer observer);
     //TODO zmienic string na MessageTemplate
     void notifyObservers(String string);
}
