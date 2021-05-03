package it.polimi.ingsw.model.gameboard.playerdashboard;

import it.polimi.ingsw.model.Goods;
import it.polimi.ingsw.model.enumeration.Resource;

/**
 * Vault Class describes the Vault of a player that can store unlimited Resources of any type
 *
 * @author Francesco Leone
 */
public class Vault {
    private int coinsAmount;
    private int servantsAmount;
    private int stoneAmount;
    private int shieldsAmount;

    public Vault() {
        coinsAmount = 0;
        servantsAmount = 0;
        stoneAmount = 0;
        shieldsAmount = 0;
    }

    public void AddResource(Resource type, int amount){
        if(type.equals(Resource.COIN)) coinsAmount += amount;
        else if(type.equals(Resource.SERVANT)) servantsAmount += amount;
        else if(type.equals(Resource.SHIELD)) shieldsAmount += amount;
        else if(type.equals(Resource.STONE)) stoneAmount += amount;
    }

    public int getResource(Resource type){
        if(type.equals(Resource.COIN)) return coinsAmount;
        else if(type.equals(Resource.SERVANT)) return servantsAmount;
        else if(type.equals(Resource.SHIELD)) return shieldsAmount;
        else return stoneAmount;
    }

    public void removeResource(Resource type, int amount){
        if(type.equals(Resource.COIN)) coinsAmount -= amount;
        else if(type.equals(Resource.SERVANT)) servantsAmount -= amount;
        else if(type.equals(Resource.SHIELD)) shieldsAmount -= amount;
        else if(type.equals(Resource.STONE)) stoneAmount -= amount;
    }

    public int checkInput(Goods needed){

        if(getResource(needed.getType()) == 0) return needed.getAmount();
        else if(getResource(needed.getType()) > needed.getAmount()) return 0;
        else return needed.getAmount() - getResource(needed.getType());
    }

    public int getCoinsAmount() {
        return coinsAmount;
    }

    public int getServantsAmount() {
        return servantsAmount;
    }

    public int getShieldsAmount() {
        return shieldsAmount;
    }

    public int getStoneAmount() {
        return stoneAmount;
    }

    public int getTotalResources(){
        return coinsAmount+servantsAmount+stoneAmount+shieldsAmount;
    }
}
