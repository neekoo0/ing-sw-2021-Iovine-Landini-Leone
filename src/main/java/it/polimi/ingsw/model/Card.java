package it.polimi.ingsw.model;

/**
 * General class used for organized the game's cards.
 * The only thing in common for all the cards are the Victory Points,
 * the other characteristics will be described in the subclasses.
 *
 * @author Lorenzo Iovine.
 */

public class Card {
    private int victoryPoints;

    public Card(int VictoryPoints) {
        this.victoryPoints = VictoryPoints;
    }

    public int getVictoryPoints(){
        return victoryPoints;
    }
}
