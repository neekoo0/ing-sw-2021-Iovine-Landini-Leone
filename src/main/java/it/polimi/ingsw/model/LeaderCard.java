package it.polimi.ingsw.model;

import java.util.ArrayList;

/**
 * This is a subclass of Card, which describes the Leader Card.
 * The things in common for each leader card are the fact that they can be activated or discarded;
 * so here we have two methods to activate or discard them,
 * and two methods that inform us if they are activated or discarded.
 *
 * @author Lorenzo Iovine.
 */

public abstract class LeaderCard extends Card{
    private boolean isDiscarded;
    private boolean isActive;

    public LeaderCard(int VictoryPoints) {
        super(VictoryPoints);
    }

    public void setIsDiscarded(){
        isDiscarded=true;
    }

    public boolean getIsDiscarded(){
        return isDiscarded;
    }

    public void setIsActive(){
        isActive=true;
    }

    public boolean getIsActive(){
        return isActive;
    }

    public ArrayList<Requirements> getRequirements() {
        return getRequirements();
    }

    public boolean checkRequirements(PlayerDashboard playerDashboard){
        return checkRequirements(playerDashboard);
    }

    public boolean checkRequirements(DevCardsSpace space){
        return checkRequirements(space);
    }
}
