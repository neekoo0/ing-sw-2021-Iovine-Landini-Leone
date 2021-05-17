package it.polimi.ingsw.model.gameboard.playerdashboard;


import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.card.deck.LeaderCardDeck;
import it.polimi.ingsw.model.card.leadercard.LeaderCard;
import it.polimi.ingsw.model.card.leadercard.StorageLeader;
import it.polimi.ingsw.model.card.leadercard.WhiteBallLeader;
import it.polimi.ingsw.model.enumeration.Resource;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class implements the dash board that belongs to every player
 *
 * @author Nicola Landini, Francesco Leone
 */

public class PlayerDashboard {

    private final FaithPath faithPath;
    private final Storage storage;
    private final Vault vault;
    private final DevCardsSpace devCardsSpace;
    private final LeaderCardDeck leaders;

    public DevCardsSpace getDevCardsSpace() {
        return devCardsSpace;
    }

    public Storage getStorage(){
        return storage;
    }

    public Vault getVault(){
        return vault;
    }

    /**
     * PlayerDashboard constructor: creates a new instance of player's dashboard
     */
    public PlayerDashboard() {
        faithPath = new FaithPath();
        vault = new Vault();
        devCardsSpace = new DevCardsSpace();
        leaders = new LeaderCardDeck();

        Shelf s1 = new Shelf(1,0, Resource.STONE);
        Shelf s2 = new Shelf(2,0,Resource.STONE);
        Shelf s3 = new Shelf(3,0,Resource.STONE);

        storage = new Storage(s1,s2,s3);
    }

    public FaithPath getFaithPath() {
        return faithPath;
    }

    public LeaderCardDeck getLeaders() {
        return leaders;
    }

    /**
     * This method checks if goods needed belongs to the player
     * @param needed goods needed
     * @return a boolean that represents if requirements are satisfied
     */
    public boolean CheckResource(ArrayList<Goods> needed){
        ArrayList<Goods> neededClone = new ArrayList<>();
        boolean RequirementsSatisfied = true;

        for(Goods g: needed){
            neededClone.add(new Goods(g));
        }


        //Storage check
        for(Goods g1: neededClone){
            g1.setAmount(getStorage().checkInput(g1));
        }

        //StorageLeader check
        for(int i = 0; i < leaders.size(); i++) {
            if (leaders.get(i) instanceof StorageLeader && leaders.get(i).getIsActive()) {
                for (Goods g1 : neededClone) {
                    if (g1.getType().equals(((StorageLeader) leaders.get(i)).getType())) {
                        if (((StorageLeader) leaders.get(i)).getAmount() >= g1.getAmount()) g1.setAmount(0);
                        else g1.setAmount(g1.getAmount() - ((StorageLeader) leaders.get(i)).getAmount());
                    }
                }
            }
        }

        //vaultCheck
        for(Goods g1: neededClone){
            g1.setAmount(getVault().checkInput(g1));
        }

        for(Goods g1: neededClone){
            if (g1.getAmount() > 0) {
                RequirementsSatisfied = false;
                break;
            }
        }

        return  RequirementsSatisfied;


    }

    /**
     * This method at first removes goods from storage, then from the eventual
     * storage leaders and finally from the vault
     * @param toRemove goods that have to be removed
     */
    public void RemoveResource(ArrayList<Goods> toRemove) {

        HashMap<Resource, Integer> ResToRemove = new HashMap<>();
        ResToRemove.put(Resource.COIN, 0);
        ResToRemove.put(Resource.SERVANT, 0);
        ResToRemove.put(Resource.SHIELD, 0);
        ResToRemove.put(Resource.STONE, 0);

        for(Goods g: toRemove){
            ResToRemove.replace(g.getType(),ResToRemove.get(g.getType()), ResToRemove.get(g.getType()) + g.getAmount());

        }

        //Storage remove
        for(Map.Entry<Resource,Integer> mapElement: ResToRemove.entrySet()){
            Resource r = mapElement.getKey();
            int remaining = getStorage().DiscardResources(mapElement.getKey(),mapElement.getValue());
            mapElement.setValue(remaining);
        }

        //StorageLeader remove
        for(int i = 0; i < leaders.size(); i++) {
            if (leaders.get(i) instanceof StorageLeader && leaders.get(i).getIsActive()) {
                Resource type = ((StorageLeader) leaders.get(i)).getType();
                int remaining = ((StorageLeader) leaders.get(i)).DiscardResources(ResToRemove.get(type));
                ResToRemove.replace(type, ResToRemove.get(type), remaining);
            }
        }

        //vault remove
        for(Map.Entry<Resource,Integer> mapElement: ResToRemove.entrySet()){
            Resource r = mapElement.getKey();
            getVault().removeResource(r, mapElement.getValue());
        }
    }

    /**
     * This method adds resources to the vault
     * @param toAdd goods that have to be added
     */
    public void AddResources(ArrayList<Goods> toAdd){
        HashMap<Resource, Integer> ResToAdd = new HashMap<>();
        ResToAdd.put(Resource.COIN, 0);
        ResToAdd.put(Resource.SERVANT, 0);
        ResToAdd.put(Resource.SHIELD, 0);
        ResToAdd.put(Resource.STONE, 0);

        for(Goods g: toAdd){
            ResToAdd.replace(g.getType(),ResToAdd.get(g.getType()), ResToAdd.get(g.getType()) + g.getAmount());
        }

        for(Map.Entry<Resource,Integer> mapElement: ResToAdd.entrySet()){
            Resource r = mapElement.getKey();
            vault.AddResource(r, mapElement.getValue());
        }
    }

    public Boolean bothWhiteLeader(){
        int white = 0;
        for(LeaderCard c : leaders.getDeck()){
            if(c instanceof WhiteBallLeader && c.getIsActive())
                white++;
        }
        return white > 1;
    }

}
