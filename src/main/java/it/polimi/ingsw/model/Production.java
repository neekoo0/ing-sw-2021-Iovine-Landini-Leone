package it.polimi.ingsw.model;

import java.util.ArrayList;

/**
 * In this class we insert all the productions requested by the
 * player before removing and adding the resources.
 *
 * @author Lorenzo Iovine.
 */

public class Production {
    private ArrayList<Goods> inputProduction;
    private ArrayList<Goods> outputProduction;
    private final int faithSteps;

    public Production(ArrayList<Goods> inputProduction, ArrayList<Goods> outputProduction, int faithSteps){
        this.inputProduction = inputProduction;
        this.outputProduction = outputProduction;
        this.faithSteps = faithSteps;
    }

    public void setInputProduction(Goods input){
        inputProduction.add(input);
    }

    public void setOutputProduction(Goods output){
        outputProduction.set(0, output);
    }

    public ArrayList<Goods> getInputProduction() {
        return inputProduction;
    }

    public ArrayList<Goods> getOutputProduction() {
        return outputProduction;
    }

    public int getFaithSteps() {
        return faithSteps;
    }

}
