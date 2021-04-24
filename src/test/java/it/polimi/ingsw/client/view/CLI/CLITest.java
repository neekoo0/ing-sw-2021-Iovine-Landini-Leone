package it.polimi.ingsw.client.view.CLI;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.exceptions.InvalidChoiceException;
import it.polimi.ingsw.model.DevelopmentCardGridTest;
import it.polimi.ingsw.model.card.deck.DevelopmentCardDeck;
import it.polimi.ingsw.model.card.developmentcard.DevelopmentCard;
import it.polimi.ingsw.model.enumeration.CardColor;
import it.polimi.ingsw.model.gameboard.DevelopmentCardGrid;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class CLITest {


    @Test
    public void printDevelopmentCard() throws FileNotFoundException, InvalidChoiceException {
        DevelopmentCardGrid d = new DevelopmentCardGrid();
        CLI cli = new CLI();

        ArrayList<DevelopmentCardDeck> deck = d.getDevCardsDecks();

        for(DevelopmentCardDeck p : deck){
            for(DevelopmentCard c : p.getDeck()){
                System.out.println(cli.printDevelopmentCard(c) + c.toString());

            }
        }
    }
}