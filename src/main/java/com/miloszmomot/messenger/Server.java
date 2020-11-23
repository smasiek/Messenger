package com.miloszmomot.messenger;


import java.util.ArrayList;

public class Server implements Subject{
    private ArrayList<Observer> observers=new ArrayList<Observer>();

    public void addObserver(Observer observer){
        observers.add(observer);
    }
    public void removeObserver(Observer observer){
        int observerIndex=observers.indexOf(observer);
        System.out.println("Observer number " + observerIndex + " removed.");

        observers.remove(observer);
    }
    //TODO zmienic string na MessageTemplate
    public void notifyObservers(String string){
        for (Observer observer : observers) {
            observer.update(string);
        }
    }

}
