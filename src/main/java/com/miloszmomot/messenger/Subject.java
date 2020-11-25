package com.miloszmomot.messenger;

public interface Subject {
     public abstract void addObserver(Observer observer);
     public abstract void removeObserver(Observer observer);
     //TODO zmienic string na MessageTemplate
     public abstract void notifyObservers(String string);


}
