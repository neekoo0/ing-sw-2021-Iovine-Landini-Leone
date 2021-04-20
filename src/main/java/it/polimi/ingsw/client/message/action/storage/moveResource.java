package it.polimi.ingsw.client.message.action.storage;

import it.polimi.ingsw.client.message.Message;


public class moveResource implements Message {
    private final int source;
    private final int destination;

    public moveResource(int source, int destination) {
        this.source = source;
        this.destination = destination;
    }

    public int getSource() {
        return source;
    }

    public int getDestination() {
        return destination;
    }
}