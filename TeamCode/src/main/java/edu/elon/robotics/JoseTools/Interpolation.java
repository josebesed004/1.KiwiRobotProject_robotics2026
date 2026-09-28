package edu.elon.robotics.JoseTools;
//Jose Random thing lol. Meant to create interpolation for beggining and end of route
public class Interpolation {
    int startNumber;
    int endNumber;
    int currentNumber;

    public Interpolation(int startNumber, int endNumber){
        this.startNumber = startNumber;
        this.endNumber = endNumber;
        this.currentNumber = startNumber;
    }

    public int next(){
        if (currentNumber < endNumber){
            currentNumber++;
        }
        return currentNumber;
    }
}
