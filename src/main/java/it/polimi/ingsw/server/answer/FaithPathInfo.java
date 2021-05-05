package it.polimi.ingsw.server.answer;

import it.polimi.ingsw.model.gameboard.playerdashboard.FaithPath;

public class FaithPathInfo implements Answer{
    int position;
    boolean Papal1;
    boolean Papal2;
    boolean Papal3;
    String message;

    public FaithPathInfo(String message, FaithPath path) {
        this.message= message;
        position= path.getPositionFaithPath();
        Papal1= path.isPapalPawn1();
        Papal2= path.isPapalPawn2();
        Papal3= path.isPapalPawn3();
    }

    public int getPosition() {
        return position;
    }

    public boolean isPapal1() {
        return Papal1;
    }

    public boolean isPapal2() {
        return Papal2;
    }

    public boolean isPapal3() {
        return Papal3;
    }

    @Override
    public String getMessage() {
        return message;
    }
}