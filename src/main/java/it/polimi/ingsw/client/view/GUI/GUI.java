package it.polimi.ingsw.client.view.GUI;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.Handler;
import it.polimi.ingsw.client.message.SendInt;
import it.polimi.ingsw.client.message.SendString;
import it.polimi.ingsw.client.view.GUI.sceneControllers.*;
import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.model.gameboard.Market;
import it.polimi.ingsw.model.singleplayer.ActionToken;
import it.polimi.ingsw.server.answer.infoanswer.DevCardsSpaceInfo;
import it.polimi.ingsw.server.answer.infoanswer.FaithPathInfo;
import it.polimi.ingsw.server.answer.infoanswer.StorageInfo;
import it.polimi.ingsw.server.answer.seegameboard.SeeBall;
import it.polimi.ingsw.server.answer.turnanswer.ActiveLeader;
import it.polimi.ingsw.server.answer.turnanswer.DiscardLeader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;


public class GUI extends Application implements View {
    private Stage stage;
    private Stage secondaryStage;

    private Scene currentScene;
    private Scene MenuScene;
    private Scene SetupScene;
    private Scene NicknameScene;
    private Scene ChooseNumberScene;
    private Scene LoadingScene;
    private Scene GameScene;
    private Scene MarketScene;
    private Scene GridScene;

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
    public static final String SINGLE_PLAYER = "SinglePlayer";
    public static final String LOCAL_SP = "setupLocalSP";
    public static final String WELCOME = "Welcome";
    public static final String GRID = "Grid";


    Handler handler;

    private final Object lock = new Object();
    private boolean notReady = true;

    private String IP;
    private int portNumber;

    private boolean isMyTurn = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        FXMLLoader menu = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
        MenuScene = new Scene(menu.load());
        mainMenuController = menu.getController();
        mainMenuController.setGui(this);

        FXMLLoader setup = new FXMLLoader(getClass().getResource("/fxml/setup.fxml"));
        SetupScene = new Scene(setup.load());
        setupController = setup.getController();
        setupController.setGui(this);

        FXMLLoader nickname = new FXMLLoader(getClass().getResource(("/fxml/Nickname.fxml")));
        NicknameScene = new Scene(nickname.load());
        nicknameController = nickname.getController();
        nicknameController.setGui(this);


        FXMLLoader number = new FXMLLoader(getClass().getResource("/fxml/PlaYerNumber.fxml"));
        ChooseNumberScene = new Scene(number.load());
        playerNumberController = number.getController();
        playerNumberController.setGui(this);

        FXMLLoader loading = new FXMLLoader(getClass().getResource(("/fxml/loading.fxml")));
        LoadingScene = new Scene(loading.load());

        FXMLLoader game = new FXMLLoader(getClass().getResource("/fxml/GameScene.fxml"));
        GameScene = new Scene(game.load());
        gameSceneController = game.getController();
        gameSceneController.setGui(this);

        FXMLLoader market = new FXMLLoader(getClass().getResource("/fxml/Market.fxml"));
        MarketScene = new Scene(market.load());
        marketSceneController = market.getController();
        marketSceneController.setGui(this);

        FXMLLoader grid = new FXMLLoader(getClass().getResource("/fxml/DevelopmentCardsGrid.fxml"));
        GridScene = new Scene(grid.load());
        developmentCardsGridController = grid.getController();
        developmentCardsGridController.setGui(this);

        sceneMap.put(MENU, MenuScene);
        sceneMap.put(SETUP, SetupScene);
        sceneMap.put(NICKNAME, NicknameScene);
        sceneMap.put(NUMBER, ChooseNumberScene);
        sceneMap.put(LOADING, LoadingScene);
        sceneMap.put(GAME, GameScene);
        sceneMap.put(MARKET, MarketScene);
        sceneMap.put(GRID, GridScene);
    }

    @Override
    public void start(Stage primaryStage) {


        stage = primaryStage;
        stage.setTitle("I Maestri del Rinascimento");
        stage.centerOnScreen();
        stage.setHeight(810);
        stage.setWidth(1440);
        stage.setFullScreen(true);
        stage.setMaximized(true);
        stage.show();

        secondaryStage = new Stage();

        Client client = new Client(this);
        Thread clientThread = new Thread(client);
        clientThread.start();

    }

    public void changeStage(String scene){
        currentScene = sceneMap.get(scene);
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
            return mainMenuController.getRis();
        }
    }

    @Override
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void setupConnection() {
        notReady = true;
        Platform.runLater(()-> setupController.setupConnection());
    }

    public void errorHandling(String error) {
        switch (error) {
            case "setup" : Platform.runLater(() -> {
                errorDialog("Wrong Setup configuration!");
                setupConnection();
            });
                break;
            default : Platform.runLater(() -> errorDialog("Generic Error!"));
        }
    }


    private void errorDialog(String error) {
        Alert errorDialog = new Alert(Alert.AlertType.ERROR);
        errorDialog.setTitle("Game Error");
        errorDialog.setHeaderText("Error!");
        errorDialog.setContentText(error);
        errorDialog.showAndWait();
    }


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

    @Override
    public void handShake(String welcome) {
        handler.send(new SendString("Client connected"));
    }

    @Override
    public void askPlayerNumber(String message) {
        Platform.runLater(()-> playerNumberController.setupPlayersNumber());
    }

    @Override
    public void askNickname(String message) {
       Platform.runLater(() -> nicknameController.setupNickname());

    }

    @Override
    public void readMessage(String message) {
        if(message.equals("You are now in the waiting room. The game will start soon!")){
            Platform.runLater(()->{
                changeStage(LOADING);
                ProgressBar progressBar = (ProgressBar) LoadingScene.lookup("#progressBar");
                progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            });
        }

        if(message.equals("The game start!")){
            Platform.runLater(()-> changeStage(GAME));
        }

    }

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

    @Override
    public void askTurnType(String message) {
        Platform.runLater(()->gameSceneController.askTurn());
    }

    @Override
    public void activeLeader(ActiveLeader message) {

    }

    @Override
    public void discardLeader(DiscardLeader message) {

    }

    @Override
    public void seeGameBoard(String message) {
        Platform.runLater(()-> {
            gameSceneController.seePhase();
            changeStage(GAME);
        }
        );
    }

    @Override
    public void seeLeaderCards(ArrayList<Integer> leaderCards) {

    }

    @Override
    public void seeMarket(Market market) {
        Platform.runLater(()->{
            marketSceneController.updateMarket(market);
            changeStage(MARKET);
            marketSceneController.seePhase();
        });
    }

    @Override
    public void chooseLine(String message) {
        handler.send(new SendInt(8));
    }

    @Override
    public void seeGrid(ArrayList<Integer> devCards) {
        Platform.runLater( () -> {
            developmentCardsGridController.updateGrid(devCards);
            changeStage(GRID);
            developmentCardsGridController.seePhase();
        });
    }

    @Override
    public void seeProductions(ArrayList<Integer> productions) {

    }

    @Override
    public void printFaithPath(FaithPathInfo path) {

    }

    @Override
    public void printStorage(StorageInfo storageInfo) {

    }

    @Override
    public void printStorageAndVault(StorageInfo storageInfo) {

    }

    @Override
    public void printDevelopmentCardsSpace(DevCardsSpaceInfo devCardsSpaceInfo) {

    }

    @Override
    public void printActionToken(ActionToken actionToken) {

    }

    @Override
    public void ManageStorage(String message) {

    }

    @Override
    public void MoveShelves(String message) {

    }

    @Override
    public void resetCard(int pos) {

    }

    @Override
    public void useMarket(String message) {
        marketSceneController.usePhase();
    }

    @Override
    public void chooseWhiteBallLeader(String message) {

    }

    @Override
    public void seeBall(SeeBall ball) {

    }

    @Override
    public void chooseShelf() {

    }

    @Override
    public void askColor(String message) {

    }

    @Override
    public void askLevel(String message) {

    }

    @Override
    public void askSpace(String message) {

    }

    @Override
    public void askType(String message) {

    }

    @Override
    public void askInput(String message) {

    }

    @Override
    public void askOutput(String message) {

    }

    @Override
    public void askDevelopmentCard(String message) {

    }

    @Override
    public void askLeaderCard(String message) {

    }

    @Override
    public void endTurn(String message) {

    }

    @Override
    public void win(String message) {

    }

    @Override
    public void lose(String message) {

    }

    @Override
    public void seeOtherCards(ArrayList<Integer> leaderCards) {

    }

    @Override
    public void seeMoreFromTheGameBoard() {

    }

    @Override
    public void setIsMyTurn(boolean isMyTurn) {
       if(isMyTurn)
           gameSceneController.isMyTurn();
       else
           gameSceneController.notMyTurn();
    }

    @Override
    public void waitForYourTurn() {
        //gameSceneController.notMyTurn();
    }


    public void setNickname(String s) {
        gameSceneController.setNicknameLabel(s);
    }
}
