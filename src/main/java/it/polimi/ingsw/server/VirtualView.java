package it.polimi.ingsw.server;

import it.polimi.ingsw.model.enumeration.Resource;
import it.polimi.ingsw.model.gameboard.Ball;
import it.polimi.ingsw.model.gameboard.Market;
import it.polimi.ingsw.model.gameboard.playerdashboard.*;
import it.polimi.ingsw.model.singleplayer.ActionToken;
import it.polimi.ingsw.observer.VirtualViewObservable;
import it.polimi.ingsw.server.answer.*;
import it.polimi.ingsw.server.answer.DiscardResource;
import it.polimi.ingsw.server.answer.initialanswer.*;
import it.polimi.ingsw.server.answer.turnanswer.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VirtualView extends VirtualViewObservable {
    private String answer;
    private final Map<String, ClientHandler> namesToClient = new HashMap<>();

    public String askHandShake(ClientHandler client) throws InterruptedException {
        client.send(new Connection("Welcome to this fantastic server!", true));
        synchronized (client.getLock()) {
            while (!client.isReady()) {
                client.getLock().wait();
            }
            answer = client.getAnswer();
        }
        client.setReady(false);
        return answer;
    }

    public String requestNickname(ClientHandler client){
        client.send(new RequestNickname("Please insert your nickname:"));
        synchronized (client.getLock()) {
            while(!client.isReady()) {
                try {
                    client.getLock().wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            answer = client.getAnswer();
        }
        client.setReady(false);
        return answer;
    }


    public String InvalidNickname(ClientHandler client) throws InterruptedException{
        client.send(new InvalidNickname("The chosen nickname is not valid. Try again:"));
        synchronized (client.getLock()) {
            while(!client.isReady()) client.getLock().wait();
            answer = client.getAnswer();
        }
        client.setReady(false);
        return answer;
    }


    public String requestPlayersNumber(ClientHandler client) throws InterruptedException {
        client.send(new PlayersNumber("Please insert the number of players:"));
        synchronized (client.getLock()) {
            while (!client.isReady()) client.getLock().wait();
            answer = client.getAnswer();
        }
        client.setReady(false);
        return answer;
        //notify everyone the players number
    }

    public void prepareTheLobby() throws IOException {
        notifyPreparationOfLobby();
    }


    public void firstPlayer(String nickname) {
        namesToClient.get(nickname).send(new FirstPlayer("You are the first player."));
    }


    public int chooseResource(String nickname, String player, int amount) throws  InterruptedException {
        ClientHandler client=namesToClient.get(nickname);

        if(player.equals("fourth") && amount==2){
            client.send(new ChooseResource("Please choose another resource."));
        } else {
            client.send(new ChooseResource("You are the " + player + " player. Please choose a resource."));
        }

        synchronized (client.getLock()){
            while(!client.isReady()) client.getLock().wait();
            answer=client.getAnswer();
        }

        client.setReady(false);

        return Integer.parseInt(answer);
    }


    public void waitingRoom(ClientHandler client){
        client.send(new WaitingRoom("You are now in the waiting room. The game will start soon!"));
    }


    public int discardFirstLeaders(String nickname, int number, ArrayList<Integer> IdLeader) throws InterruptedException {
        ClientHandler client=namesToClient.get(nickname);

        if(number==1) {
            client.send(new DiscardFirstLeaders("Please choose the first leader card to discard."));
        } else {
            client.send(new DiscardFirstLeaders("Please choose the second leader card to discard."));
        }
        client.send(new PassLeaderCard(IdLeader));
        synchronized (client.getLock()) {
            while (!client.isReady()) client.getLock().wait();
            answer = client.getAnswer();
        }

        client.setReady(false);

        return Integer.parseInt(answer);
    }


    public void startGame(String nickname) {
        ClientHandler client=namesToClient.get(nickname);

        client.send(new StartGame("The game start!"));
    }


    public void seeFaithPath(String nickname, String player, FaithPath path, boolean SinglePlayer) {
        ClientHandler client=namesToClient.get(nickname);

        client.send(new FaithPathInfo(("This is the Dashboard of "+player+" :"), path, SinglePlayer));
    }


    public void seeStorage(String nickname, Storage storage, Vault vault) {
        ClientHandler client=namesToClient.get(nickname);

        client.send(new StorageInfo(storage, vault));
    }


    public void seeDevCardsSpace(String nickname, DevCardsSpace space) {
        ClientHandler client=namesToClient.get(nickname);

        client.send(new DevCardsSpaceInfo(space));
    }

    public void seeActionToken(String nickname, ActionToken actionToken){
        ClientHandler client=namesToClient.get(nickname);

        client.send(new ActionTokenInfo(actionToken));
    }


    public int seeGameBoard(String nickname) throws InterruptedException {
        ClientHandler client=namesToClient.get(nickname);

        client.send(new SeeGameBoard("What do you want to see about the Game Board?"));
        synchronized (client.getLock()) {
            while(!client.isReady()) client.getLock().wait();
            answer=client.getAnswer();
        }

        client.setReady(false);

        return Integer.parseInt(answer);
    }


    public int seeLeaderCards(String nickname, ArrayList<Integer> leaderCards) throws InterruptedException {
        ClientHandler client=namesToClient.get(nickname);

        client.send(new SeeLeaderCards(leaderCards));
        synchronized (client.getLock()) {
            while(!client.isReady()) client.getLock().wait();
            answer=client.getAnswer();
        }

        client.setReady(false);

        return Integer.parseInt(answer);
    }

    public int seeMarket(String nickname, Market market) throws InterruptedException {
        ClientHandler client=namesToClient.get(nickname);

        client.send(new SeeMarket(market));
        synchronized (client.getLock()) {
            while(!client.isReady()) client.getLock().wait();
            answer=client.getAnswer();
        }

        client.setReady(false);

        return Integer.parseInt(answer);
    }

    public int chooseLine(String nickname) throws InterruptedException {
        ClientHandler client=namesToClient.get(nickname);

        client.send(new ChooseLine("Please choose what you want to see from the Development Cards Grid"));
        synchronized (client.getLock()) {
            while(!client.isReady()) client.getLock().wait();
            answer=client.getAnswer();
        }

        client.setReady(false);

        return Integer.parseInt(answer);
    }

    public int seeGrid(String nickname, ArrayList<Integer> id) throws InterruptedException {
        ClientHandler client=namesToClient.get(nickname);

        client.send(new SeeGrid(id));

        synchronized (client.getLock()) {
            while(!client.isReady()) client.getLock().wait();
            answer=client.getAnswer();
        }

        client.setReady(false);

        return Integer.parseInt(answer);
    }

    public int seeProduction(String nickname, ArrayList<Integer> productions) throws InterruptedException {
        ClientHandler client=namesToClient.get(nickname);

        client.send(new SeeProductions(productions));

        synchronized (client.getLock()) {
            while(!client.isReady()) client.getLock().wait();
            answer=client.getAnswer();
        }

        client.setReady(false);

        return Integer.parseInt(answer);
    }


    public int chooseTurn(String nickname) throws InterruptedException {
        ClientHandler client=namesToClient.get(nickname);

        client.send(new ChooseTurn("Choose what you want to do in this turn:"));
        synchronized (client.getLock()) {
            while(!client.isReady()) client.getLock().wait();
            answer=client.getAnswer();
        }

        client.setReady(false);

        return Integer.parseInt(answer);
    }

    public int activeLeader(String nickname, ArrayList<Integer> leaders) throws InterruptedException {
        ClientHandler client=namesToClient.get(nickname);

        client.send(new ActiveLeader("Which leader do you want to activate?", leaders));
        synchronized (client.getLock()) {
            while(!client.isReady()) client.getLock().wait();
            answer=client.getAnswer();
        }

        client.setReady(false);

        return Integer.parseInt(answer);
    }

    public int discardLeader(String nickname, ArrayList<Integer> leaders) throws InterruptedException {
        ClientHandler client=namesToClient.get(nickname);

        client.send(new DiscardLeader("Which leader do you want to discard?", leaders));
        synchronized (client.getLock()) {
            while(!client.isReady()) client.getLock().wait();
            answer=client.getAnswer();
        }

        client.setReady(false);

        return Integer.parseInt(answer);
    }

    public int manageStorage(int number, String nickname) throws InterruptedException {
        ClientHandler client=namesToClient.get(nickname);

        if(number==1)
            client.send(new ManageStorage("Before using the market do you want to reorder your storage? You won't be able to do it later."));
        else
            client.send(new ManageStorage("Do you want to make other changes to the storage?"));

        synchronized (client.getLock()) {
            while(!client.isReady()) client.getLock().wait();
            answer=client.getAnswer();
        }

        client.setReady(false);

        return Integer.parseInt(answer);
    }

    public  ArrayList<Integer> moveShelves(String nickname) throws InterruptedException {
        ClientHandler client=namesToClient.get(nickname);
        ArrayList<Integer> moves=new ArrayList<>();

        client.send(new MoveShelves("Which shelves do you want to reverse?"));

        synchronized (client.getLock()) {
            while(!client.isReady()) client.getLock().wait();
            answer=client.getAnswer();
            moves.add(Integer.parseInt(answer));
        }

        client.setReady(false);

        synchronized (client.getLock()) {
            while(!client.isReady()) client.getLock().wait();
            answer=client.getAnswer();
            moves.add(Integer.parseInt(answer));
        }

        client.setReady(false);

        return moves;
    }

    public int useMarket(String nickname) throws InterruptedException {
        ClientHandler client=namesToClient.get(nickname);

        client.send(new UseMarket("Which market line do you want?"));

        synchronized (client.getLock()) {
            while(!client.isReady()) client.getLock().wait();
            answer=client.getAnswer();
        }

        client.setReady(false);

        return Integer.parseInt(answer);
    }

    public ArrayList<Integer> seeBall(String nickname, ArrayList<Ball> balls) throws InterruptedException {
        ClientHandler client=namesToClient.get(nickname);
        ArrayList<Integer> moves=new ArrayList<>();

        client.send(new SeeBall(balls));

        synchronized (client.getLock()) {
            while(!client.isReady()) client.getLock().wait();
            answer=client.getAnswer();
            moves.add(Integer.parseInt(answer));
        }

        client.setReady(false);

        synchronized (client.getLock()) {
            while(!client.isReady()) client.getLock().wait();
            answer=client.getAnswer();
            moves.add(Integer.parseInt(answer));
        }

        client.setReady(false);

        return moves;
    }

    public int askWhiteBallLeader(String nickname) throws InterruptedException {
        ClientHandler client=namesToClient.get(nickname);

        client.send(new ChooseTwoWhiteBallLeader("You have 2 active WhiteBall leaders, which one do you want to use in this turn? (1 or 2)"));

        synchronized (client.getLock()) {
            while(!client.isReady()) client.getLock().wait();
            answer=client.getAnswer();
        }

        client.setReady(false);

        return Integer.parseInt(answer);
    }

    public int askColor(String nickname) throws InterruptedException {
        ClientHandler client=namesToClient.get(nickname);

        client.send(new AskColor("Choose the color of the card you want to buy.\n1) Purple\n2) Yellow\n3) Blue\n4) Green"));

        synchronized (client.getLock()) {
            while(!client.isReady()) client.getLock().wait();
            answer=client.getAnswer();
        }

        client.setReady(false);

        return Integer.parseInt(answer);
    }

    public int askLevel(String nickname) throws InterruptedException {
        ClientHandler client=namesToClient.get(nickname);

        client.send(new AskLevel("Choose the level of the card you want to buy"));

        synchronized (client.getLock()) {
            while(!client.isReady()) client.getLock().wait();
            answer=client.getAnswer();
        }

        client.setReady(false);

        return Integer.parseInt(answer);
    }

    public int askSpace(String nickname) throws InterruptedException {
        ClientHandler client=namesToClient.get(nickname);

        client.send(new AskSpace("Choose the space where to insert the card"));

        synchronized (client.getLock()) {
            while(!client.isReady()) client.getLock().wait();
            answer=client.getAnswer();
        }

        client.setReady(false);

        return Integer.parseInt(answer);
    }

    public int askType(String nickname) throws InterruptedException {
        ClientHandler client=namesToClient.get(nickname);

        client.send(new AskType("What kind of production do you want to activate?\n1) Basic Production\n2) Development Card\n3) Leader Card\n4) It's okay, do productions"));

        synchronized (client.getLock()) {
            while(!client.isReady()) client.getLock().wait();
            answer=client.getAnswer();
        }

        client.setReady(false);

        return Integer.parseInt(answer);
    }

    public Resource askInput(String nickname) throws InterruptedException {
        ClientHandler client=namesToClient.get(nickname);

        client.send(new AskInput("Choose the input:\n1) COIN\n2) SERVANT\n3) SHIELD\n4) STONE"));

        synchronized (client.getLock()) {
            while(!client.isReady()) client.getLock().wait();
            answer=client.getAnswer();
        }

        client.setReady(false);

        switch(Integer.parseInt(answer)) {
            case 1 : return Resource.COIN;
            case 2 : return Resource.SERVANT;
            case 3 : return Resource.SHIELD;
            case 4 : return Resource.STONE;
            default : return null;
        }
    }

    public Resource askOutput(String nickname) throws InterruptedException {
        ClientHandler client=namesToClient.get(nickname);

        client.send(new AskOutput("Choose the output:\n1) COIN\n2) SERVANT\n3) SHIELD\n4) STONE"));

        synchronized (client.getLock()) {
            while(!client.isReady()) client.getLock().wait();
            answer=client.getAnswer();
        }

        client.setReady(false);

        switch(Integer.parseInt(answer)) {
            case 1 : return Resource.COIN;
            case 2 : return Resource.SERVANT;
            case 3 : return Resource.SHIELD;
            case 4 : return Resource.STONE;
            default : return null;
        }
    }

    public int askDevCard(String nickname) throws InterruptedException {
        ClientHandler client=namesToClient.get(nickname);

        client.send(new AskDevelopmentCard("Insert the number of the space containing the development card"));

        synchronized (client.getLock()) {
            while(!client.isReady()) client.getLock().wait();
            answer=client.getAnswer();
        }

        client.setReady(false);

        return Integer.parseInt(answer);
    }

    public int askLeaderCard(String nickname) throws InterruptedException {
        ClientHandler client=namesToClient.get(nickname);

        client.send(new AskDevelopmentCard("Insert the number of the production leader that you want to use"));

        synchronized (client.getLock()) {
            while(!client.isReady()) client.getLock().wait();
            answer=client.getAnswer();
        }

        client.setReady(false);

        return Integer.parseInt(answer);
    }

    public int endTurn(String nickname) throws InterruptedException {
        ClientHandler client=namesToClient.get(nickname);

        client.send(new AskDevelopmentCard("What do you want to do?\n1) Active Leader\n2) Discard Leader\n3) End turn"));

        synchronized (client.getLock()) {
            while(!client.isReady()) client.getLock().wait();
            answer=client.getAnswer();
        }

        client.setReady(false);

        return Integer.parseInt(answer);
    }

    public void sendErrorMessage(String nickname){
        ClientHandler client=namesToClient.get(nickname);

        client.send(new GameError("Invalid choice."));
    }

    public void resetCard(String nickname, int pos) {
        ClientHandler client=namesToClient.get(nickname);

        client.send(new ResetCard(pos));
    }


    public void setNamesToClient(String nickname, ClientHandler client) {
        namesToClient.put(nickname,client);
    }

    public void removeNamesToClient(String nickname, ClientHandler client){
        namesToClient.remove(nickname, client);
    }

    public void sendTurnStatus(String message, String nickname){
        if(message.equals("START")){
            notifyPlayingNick(nickname);
        }

        ClientHandler client = namesToClient.get(nickname);

        client.send(new TurnStatus(message));
    }
}
