package it.polimi.ingsw.server;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.observer.ConnectionObserver;
import it.polimi.ingsw.observer.VirtualViewObserver;
import it.polimi.ingsw.server.answer.initialanswer.JoiningPlayer;
import it.polimi.ingsw.server.answer.initialanswer.PrepareTheLobby;
import it.polimi.ingsw.server.answer.turnanswer.Disconnection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;


public class Lobby implements ConnectionObserver, VirtualViewObserver {
    private int playersNumber;
    private final int lobbyID;
    private final VirtualView view=new VirtualView();
    private final Map<ClientHandler, String> clientToNames = new HashMap<>();
    private final Map<String, ClientHandler> namesToClient = new HashMap<>();
    private final ArrayList<String> nicknames = new ArrayList<>();

    public Lobby(int lobbyID) {
        this.lobbyID = lobbyID;
    }

    public void newLobby(ClientHandler firstClient) throws InterruptedException {
        String s = view.askHandShake(firstClient);
        System.out.println(s);

        s = view.requestNickname(firstClient);
        clientToNames.put(firstClient, s);
        namesToClient.put(s, firstClient);
        nicknames.add(s);
        view.setNamesToClient(s,firstClient);


        firstClient.registerObserver(this);
        view.registerObserver(this);


        String str = clientToNames.get(firstClient);
        System.out.println(str + " has joined the lobby number " + lobbyID);

        playersNumber = Integer.parseInt(view.requestPlayersNumber(firstClient) );
        System.out.println("The lobby number "+ lobbyID +" will contain " + playersNumber + " players");

        view.waitingRoom(firstClient);
    }

    public void add(ClientHandler clientHandler) throws IOException, InterruptedException {
        String s=view.askHandShake(clientHandler);
        System.out.println(s);

        s = view.requestNickname(clientHandler);

        while(nicknames.contains(s) || s.equals("laulo") || s.equals("Laulo")){
            s=view.InvalidNickname(clientHandler);
        }


        for(String nick: nicknames){
            namesToClient.get(nick).send(new JoiningPlayer(Constants.ANSI_BLUE +  s + " joins the game. "+(nicknames.size()+1)+" players have already joined this Lobby."+Constants.ANSI_RESET));
        }


        clientToNames.put(clientHandler,s);
        namesToClient.put(s, clientHandler);
        nicknames.add(s);
        view.setNamesToClient(s,clientHandler);


        clientHandler.registerObserver(this);


        if(nicknames.size()<playersNumber){
            view.waitingRoom(clientHandler);
        } else {
            view.waitingRoom(clientHandler);
            view.prepareTheLobby();
        }

        String str= clientToNames.get(clientHandler);
        System.out.println(str + " has joined the lobby number " + lobbyID);
    }

    public void removeConnection(ClientHandler clientHandler){
        System.out.println("Removing player "+clientToNames.get(clientHandler));
        namesToClient.remove(clientToNames.get(clientHandler));
        nicknames.remove(clientToNames.get(clientHandler));
        view.removeNamesToClient(clientToNames.get(clientHandler), clientHandler);
        clientToNames.remove(clientHandler);
        playersNumber=-1;
    }

    @Override
    public void updateDisconnection(ClientHandler clientHandler) {
        //removeConnection(clientHandler);
        clientHandler.unregisterObserver(this);
        for(String nick: nicknames){
            namesToClient.get(nick).send(new Disconnection("Player "+clientToNames.get(clientHandler)+" left the game. Now there are "+(nicknames.size()-1)+" players in this Lobby."));
        }
        removeConnection(clientHandler);
    }

    @Override
    public void updatePreparationOfLobby(){
        for(String nick: nicknames){
            namesToClient.get(nick).send(new PrepareTheLobby("All players joined the lobby. We are preparing the game!"));
        }
    }

    public int getPlayersNumber() {
        return playersNumber;
    }

    public Map<String, ClientHandler> getNamesToClient() {
        return namesToClient;
    }

    public void prepareTheGame(){
        Thread t=new Thread( () -> {
            System.out.println("Prepare game of the lobby number " + lobbyID);
            Collections.shuffle(nicknames);

            Controller controller = new Controller(nicknames, view);

            controller.play();
        }
        );

        t.start();
    }
}
