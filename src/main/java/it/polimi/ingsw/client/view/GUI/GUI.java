package it.polimi.ingsw.client.view.GUI;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.Handler;
import it.polimi.ingsw.client.message.SendInt;
import it.polimi.ingsw.client.view.GUI.sceneControllers.*;
import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.model.card.deck.DevelopmentCardDeck;
import it.polimi.ingsw.model.gameboard.Market;
import it.polimi.ingsw.model.singleplayer.ActionToken;
import it.polimi.ingsw.server.answer.infoanswer.*;
import it.polimi.ingsw.server.answer.seegameboard.InitializeGameBoard;
import it.polimi.ingsw.server.answer.seegameboard.SeeBall;
import it.polimi.ingsw.server.answer.seegameboard.UpdateFaithPath;
import it.polimi.ingsw.server.answer.seegameboard.UpdatePapalPawn;
import it.polimi.ingsw.server.answer.turnanswer.ActiveLeader;
import it.polimi.ingsw.server.answer.turnanswer.DiscardLeader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.*;


public class GUI extends Application implements View {
    private Stage stage;
    private Stage secondaryStage;

    private Scene LoadingScene;
    private Scene LocalGameScene;

    private MainMenuController mainMenuController;
    private SetupController setupController;
    private NicknameController nicknameController;
    private PlayerNumberController playerNumberController;
    private GameSceneController gameSceneController;
    private MarketSceneController marketSceneController;
    private DiscardLeaderController discardLeaderController;
    private InitialResourcesController initialResourcesController;
    private DevelopmentCardsGridController developmentCardsGridController;

    private final Map<String, Scene> sceneMap = new HashMap<>();
    public static final String MENU = "MainMenu";
    public static final String SETUP = "setup";
    public static final String NICKNAME = "Nickname";
    public static final String NUMBER = "Number";
    public static final String LOADING = "Loading";
    public static final String GAME = "Game";
    public static final String MARKET = "Market";
    public static final String GRID = "Grid";
    public final static String LOCAL_GAME = "LocalGame";


    private Handler handler;

    private final Object lock = new Object();
    private boolean notReady = true;

    private String IP;
    private int portNumber;
    private String nickname;

    private StorageInfo lastStorage;
    private boolean isSinglePlayer=false;
    private final ArrayList<Integer> leaderCards = new ArrayList<>();
    private String singlePlayerNick;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        FXMLLoader menu = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
        Scene menuScene = new Scene(menu.load());
        mainMenuController = menu.getController();
        mainMenuController.setGui(this);

        FXMLLoader setup = new FXMLLoader(getClass().getResource("/fxml/setup.fxml"));
        Scene setupScene = new Scene(setup.load());
        setupController = setup.getController();
        setupController.setGui(this);

        FXMLLoader nickname = new FXMLLoader(getClass().getResource(("/fxml/Nickname.fxml")));
        Scene nicknameScene = new Scene(nickname.load());
        nicknameController = nickname.getController();
        nicknameController.setGui(this);


        FXMLLoader number = new FXMLLoader(getClass().getResource("/fxml/PlayerNumber.fxml"));
        Scene chooseNumberScene = new Scene(number.load());
        playerNumberController = number.getController();
        playerNumberController.setGui(this);

        FXMLLoader loading = new FXMLLoader(getClass().getResource(("/fxml/loading.fxml")));
        LoadingScene = new Scene(loading.load());

        FXMLLoader game = new FXMLLoader(getClass().getResource("/fxml/GameScene.fxml"));
        Scene gameScene = new Scene(game.load());
        gameSceneController = game.getController();
        gameSceneController.setGui(this);
        gameScene.getStylesheets().add(Objects.requireNonNull(GUI.class.getResource("/bootstrap3.css")).toExternalForm());


        FXMLLoader market = new FXMLLoader(getClass().getResource("/fxml/Market.fxml"));
        Scene marketScene = new Scene(market.load());
        marketSceneController = market.getController();
        marketSceneController.setGui(this);

        FXMLLoader grid = new FXMLLoader(getClass().getResource("/fxml/DevelopmentCardsGrid.fxml"));
        Scene gridScene = new Scene(grid.load());
        developmentCardsGridController = grid.getController();
        developmentCardsGridController.setGui(this);

        sceneMap.put(MENU, menuScene);
        sceneMap.put(SETUP, setupScene);
        sceneMap.put(NICKNAME, nicknameScene);
        sceneMap.put(NUMBER, chooseNumberScene);
        sceneMap.put(LOADING, LoadingScene);
        sceneMap.put(GAME, gameScene);
        sceneMap.put(MARKET, marketScene);
        sceneMap.put(GRID, gridScene);
    }

    @Override
    public void start(Stage primaryStage) {


        stage = primaryStage;
        stage.setTitle("Master of Renaissance");
        stage.centerOnScreen();
        stage.setHeight(810);
        stage.setWidth(1440);
        stage.setFullScreen(true);
        stage.setMaximized(true);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/graphics/inkwell.png"))));
        stage.show();

        secondaryStage = new Stage();

        Client client = new Client(this);
        Thread clientThread = new Thread(client);
        clientThread.start();

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(0);
    }

    /**
     * manages the change of stage
     * @param scene is the new scene
     */
    public void changeStage(String scene){
        Scene currentScene = sceneMap.get(scene);
        stage.setScene(currentScene);
        stage.show();
    }

    public Object getLock() {
        return lock;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setNotReady(boolean notReady) {
        this.notReady = notReady;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int gameType() {
        Platform.runLater(()-> mainMenuController.start());

        synchronized (lock){
            while(notReady) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            int ris=mainMenuController.getRis();
            if(mainMenuController.getRis()==1){
                FXMLLoader localGame = new FXMLLoader(getClass().getResource("/fxml/LocalSinglePlayerBoard.fxml"));
                try {
                    LocalGameScene = new Scene(localGame.load());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                gameSceneController = localGame.getController();
                gameSceneController.setGui(this);
                sceneMap.put(GAME, LocalGameScene);
                gameSceneController.setLorenzoFaithPathMap();
            }
            return ris;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setHandler(Handler handler) {
        this.handler = handler;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setupConnection() {
        notReady = true;
        Platform.runLater(()-> setupController.setupConnection());
    }


    /**
     * manages the error received
     * @param error the error received
     */
    public void errorHandling(String error) {
        switch (error) {
            case "setup" : Platform.runLater(() -> {
                errorDialog("Wrong Setup configuration!");
                setupConnection();
            });

            case "MARKET_INVALID_SHELF": Platform.runLater(() -> marketSceneController.error(error));
                break;
            case "MARKET_INVALID_STORAGE_LEADER": Platform.runLater(() -> marketSceneController.error(error));
                break;
            default : Platform.runLater(() -> errorDialog("Generic Error!"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateGrid(ArrayList<Integer> idCards) {
        Platform.runLater(() -> developmentCardsGridController.updateGrid(idCards));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateBasicProduction(BasicProductionInfo info) {
        Platform.runLater(() -> gameSceneController.updateBasicProduction(info));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateMarket(Market market) {
        Platform.runLater(()->  marketSceneController.updateMarket(market));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDevCardsSpace(ArrayList<DevelopmentCardDeck> spaces, String owner) {
        Platform.runLater( () -> gameSceneController.setDevCardsSpaceForReconnection(spaces, owner));
    }


    private void errorDialog(String error) {
        Alert errorDialog = new Alert(Alert.AlertType.ERROR);
        errorDialog.setTitle("Game Error");
        errorDialog.setHeaderText("Error!");
        errorDialog.setContentText(error);
        errorDialog.showAndWait();
    }

    private void disconnectionAlert(String warning) {
        Alert errorDialog = new Alert(Alert.AlertType.WARNING);
        errorDialog.setTitle("Disconnection alert");
        errorDialog.setHeaderText("Warning!");
        errorDialog.setContentText(warning);
        errorDialog.showAndWait();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateDevCardsSpace(CardsSpaceInfo info) {
        Platform.runLater( () -> gameSceneController.updateCardsSpace(info));
        Platform.runLater( () -> {
            if(info.getNickname().equals(nickname)) {
                developmentCardsGridController.updateSpace(info);
            }
        });
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getIp() {
        synchronized (lock) {
            while (notReady) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return IP;
        }
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPortNumber() {
        synchronized (lock) {
            while (notReady) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return portNumber;
        }
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    /**
     * unused in GUI
     */
    @Override
    public void handShake(String welcome) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void askPlayerNumber(String message) {
        Platform.runLater(()-> playerNumberController.setupPlayersNumber());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void askNickname(String message) {
       Platform.runLater(() -> nicknameController.setupNickname(message));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void readMessage(String message) {
        if(message.equals("You are now in the waiting room. The game will start soon!")){
            Platform.runLater(()->{
                changeStage(LOADING);
                ProgressBar progressBar = (ProgressBar) LoadingScene.lookup("#progressBar");
                progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            });
        }

        else if(message.equals("The game starts!")){
            if(isSinglePlayer){
                FXMLLoader localGame = new FXMLLoader(getClass().getResource("/fxml/LocalSinglePlayerBoard.fxml"));
                try {
                    LocalGameScene = new Scene(localGame.load());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                gameSceneController = localGame.getController();
                gameSceneController.setGui(this);
                sceneMap.put(GAME, LocalGameScene);
                gameSceneController.setLorenzoFaithPathMap();

                Platform.runLater(()->changeStage(LOCAL_GAME));

                gameSceneController.setNicknameLabel(singlePlayerNick);
            } else {
                Platform.runLater(()-> changeStage(GAME));
            }
        }

        else if(message.equals("The game starts!\n")){
            Platform.runLater(()->changeStage(LOCAL_GAME));
        }

        else if(message.endsWith(" is playing") || message.startsWith(">") || message.endsWith("is back.")){
            Platform.runLater(()->gameSceneController.updateMessage(message));
        }

        else if(message.endsWith("players in this Lobby.") || message.startsWith("You crashed")){
            Platform.runLater(()->disconnectionAlert(message));
        }

        else if(message.equals("INVALID") || message.equals("Invalid choice.")) {
            Platform.runLater(() -> {
                gameSceneController.invalidChoice();
                gameSceneController.askTurn();
                changeStage(GAME);
            });
        }
        else if (message.equals("END_GAME")){
            Platform.runLater(()-> gameSceneController.endGame());
        }
        else if(message.equals("We are creating the lobby, please wait...")){
            Platform.runLater(()->
            {
                changeStage(LOADING);
                Label loading = (Label) LoadingScene.lookup("#loading_label");
                loading.setText("Please wait, we are creating the lobby...");
                ProgressBar progressBar = (ProgressBar) LoadingScene.lookup("#progressBar");
                progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void askResource(String message) {
        //1) COIN 2) STONE 3) SHIELD 4) SERVANT

        Platform.runLater(()->{
            FXMLLoader res = new FXMLLoader(getClass().getResource("/fxml/InitialResource.fxml"));
            Scene InitialResource = null;
            try {
                InitialResource = new Scene(res.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
            initialResourcesController = res.getController();
            initialResourcesController.setGui(this);

            secondaryStage.setScene(InitialResource);
            secondaryStage.centerOnScreen();
            initialResourcesController.askResource();
            secondaryStage.showAndWait();
        });

    }

    public Stage getSecondaryStage() {
        return secondaryStage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void askLeaderToDiscard(ArrayList<Integer> IdLeaders) {
        Platform.runLater(()->{
            FXMLLoader discard = new FXMLLoader(getClass().getResource("/fxml/DiscardLeader.fxml"));
            Scene DiscardLeader = null;
            try {
                DiscardLeader = new Scene(discard.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
            discardLeaderController = discard.getController();
            discardLeaderController.setGui(this);

            secondaryStage.setScene(DiscardLeader);
            secondaryStage.centerOnScreen();
            discardLeaderController.discardLeader(IdLeaders);
            secondaryStage.showAndWait();
        });

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void askTurnType(String message) {
        Platform.runLater(()->gameSceneController.askTurn());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activeLeader(ActiveLeader message) {
        Platform.runLater(() -> gameSceneController.activeLeader());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void discardLeader(DiscardLeader message) {
        Platform.runLater( () -> gameSceneController.discardLeader());
    }

    public void changeGameBoard() {
        Platform.runLater(()-> changeStage(GAME));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void seeGameBoard(String message) {
        Platform.runLater(()-> {
            gameSceneController.seePhase();
            changeStage(GAME);
        }
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void seeLeaderCards(ArrayList<Integer> leaderCards) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void seeMarket(Market market) {
        Platform.runLater(()->{
            changeStage(MARKET);
            marketSceneController.seePhase();
            marketSceneController.storage(lastStorage);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void chooseLine(String message) {
        handler.send(new SendInt(8));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void seeGrid(ArrayList<Integer> devCards) {
        Platform.runLater( () -> {
            changeStage(GRID);
            developmentCardsGridController.seePhase();
        });
    }

    /**
     * unused in GUI
     */
    @Override
    public void seeProductions(ArrayList<Integer> productions) {}

    /**
     * unused in GUI
     */
    @Override
    public void printFaithPath(FaithPathInfo path) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void printStorage(StorageInfo storageInfo) {
        Platform.runLater(()-> {
            gameSceneController.updateStorage(storageInfo);
        if(storageInfo.getNickname().equals(nickname)) {
            lastStorage = storageInfo;
            marketSceneController.storage(storageInfo);
        }
    });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printStorageAndVault(StorageInfo storageInfo) {
        Platform.runLater(()-> {
            gameSceneController.updateStorage(storageInfo);
            if(storageInfo.getNickname().equals(nickname)) {
                lastStorage = storageInfo;
                marketSceneController.storage(storageInfo);
            }
        });
    }

    /**
     * unused in GUI
     */
    @Override
    public void printDevelopmentCardsSpace(DevCardsSpaceInfo devCardsSpaceInfo) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void printActionToken(ActionToken actionToken) {
        Platform.runLater(()-> gameSceneController.updateActionToken(actionToken));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ManageStorage(String message) {
        Platform.runLater(() -> {
            marketSceneController.storage(lastStorage);
            marketSceneController.manageStorage();
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void MoveShelves(String message) {
        Platform.runLater(()->{
            marketSceneController.storage(lastStorage);
            marketSceneController.moveShelves();
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetCard(int pos) {
        gameSceneController.resetCard(pos);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void useMarket(String message) {
        Platform.runLater(()-> {
            marketSceneController.storage(lastStorage);
            marketSceneController.usePhase();
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void chooseWhiteBallLeader(String message) {
        Platform.runLater(()-> marketSceneController.whiteBallLeader());
    }

    /**
     * Shows the leader cards of the other player when active
     * @param info contains the IDs of the activated leader card and the owner
     */
    public void activeOtherLeaderCard(OtherLeaderCard info) {
        Platform.runLater( () -> gameSceneController.activeOtherLeaderCard(info));
    }

    /**
     * Deletes the leader cards of other player when deactivated
     * @param info contains the IDs of the deactivated leader card and the owner
     */
    public void discardOtherLeaderCard(OtherLeaderCard info) {
        Platform.runLater( () -> gameSceneController.discardOtherLeaderCard(info));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void seeBall(SeeBall ball) {
        Platform.runLater(()-> marketSceneController.seeBall(ball));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void chooseShelf() {
        Platform.runLater(()-> marketSceneController.chooseShelf());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void askCardToBuy(ArrayList<Integer> cards, ArrayList<Integer> spaces) {
        Platform.runLater( () -> {
            changeStage(GRID);
            developmentCardsGridController.buyCard();
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void askSpace(String message) {
        developmentCardsGridController.chooseSpace();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void askType(String message) {
        Platform.runLater(() -> gameSceneController.chooseProduction());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void askInput(String message) {
        Platform.runLater(() -> {
            FXMLLoader res = new FXMLLoader(getClass().getResource("/fxml/InitialResource.fxml"));
            Scene InitialResource = null;
            try {
                InitialResource = new Scene(res.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
            initialResourcesController = res.getController();
            initialResourcesController.setGui(this);

            initialResourcesController.setLabel("Choose the input of the production");
            secondaryStage.setScene(InitialResource);
            secondaryStage.centerOnScreen();
            initialResourcesController.askResource();
            secondaryStage.showAndWait();
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void askOutput(String message) {
        Platform.runLater(() -> {
            FXMLLoader res = new FXMLLoader(getClass().getResource("/fxml/InitialResource.fxml"));
            Scene InitialResource = null;
            try {
                InitialResource = new Scene(res.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
            initialResourcesController = res.getController();
            initialResourcesController.setGui(this);

            initialResourcesController.setLabel("Choose the output of the production");
            secondaryStage.setScene(InitialResource);
            secondaryStage.centerOnScreen();
            initialResourcesController.askResource();
            secondaryStage.showAndWait();

        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void askDevelopmentCard(String message) {
        Platform.runLater(() -> gameSceneController.developmentCardProduction());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void askLeaderCard(String message) {
        Platform.runLater(() -> gameSceneController.leaderCardProduction());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endTurn(String message) {
        Platform.runLater( () -> {
            changeStage(GAME);
            gameSceneController.endTurn();
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void win(String message) {
        Platform.runLater(() -> gameSceneController.winLabel(message));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void lose(String message) {
        Platform.runLater(() -> gameSceneController.loseLabel(message));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void seeOtherCards(ArrayList<Integer> leaderCards) {

    }

    /**
     * unused in GUI
     */
    @Override
    public void seeMoreFromTheGameBoard() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIsMyTurn(boolean isMyTurn) {
        if(isMyTurn) {
            Platform.runLater(() ->
                    gameSceneController.isMyTurn()
            );
        }
        else {
            Platform.runLater(() ->
                    gameSceneController.notMyTurn()
            );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitForYourTurn() {
    }

    /**
     * Initializes the game board at the beginning of the match
     * @param message the message containing the initial information
     */
    public void initializeGameBoard(InitializeGameBoard message) {
        marketSceneController.updateMarket(message.getMarket());
        developmentCardsGridController.updateGrid(message.getIdDevCards());
        gameSceneController.updateLeaderCards(message);
        if(!isSinglePlayer){
            gameSceneController.setInkwell();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void playersInfo(PlayersInfo playersInfo) {
        if(playersInfo.getPlayersNumber()==1){
            isSinglePlayer=true;
            singlePlayerNick=playersInfo.getNicknames().get(0);
        }
        Platform.runLater(()-> {
            changeStage(GAME);
            gameSceneController.setupGameBoard(playersInfo);
        }
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateFaithPath(UpdateFaithPath updateFaithPath){
        Platform.runLater(() -> gameSceneController.updateFaithPath(updateFaithPath));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updatePapalPawn(UpdatePapalPawn updatePapalPawn){
        Platform.runLater(() -> gameSceneController.updatePapalPawn(updatePapalPawn));
    }


    public void setNickname(String s) {
        this.nickname = s;
        gameSceneController.setNicknameLabel(s);
    }

    public String getNickname() {
        return nickname;
    }

    public Map<String, Scene> getSceneMap() {
        return sceneMap;
    }

    public ArrayList<Integer> getLeaderCards() {
        return leaderCards;
    }
}
