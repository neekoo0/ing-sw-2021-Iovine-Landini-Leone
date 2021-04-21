package it.polimi.ingsw.client;

import it.polimi.ingsw.client.message.ClientConnection;
import it.polimi.ingsw.client.message.NumberOfPlayers;
import it.polimi.ingsw.client.message.SendNickname;
import it.polimi.ingsw.client.view.CLI;
import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.server.answer.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class NetworkHandler implements Runnable {
    private Socket server;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Client owner;
    private View view;

    public NetworkHandler(Socket server, Client owner) {
        this.server = server;
        this.owner = owner;
        view = new CLI();
    }

    @Override
    public void run() {

        try {
            input = new ObjectInputStream(server.getInputStream());
            output = new ObjectOutputStream(server.getOutputStream());
        } catch (IOException e) {
            System.out.println("Could not open connection to");
        }

        try {
            handleClientConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handleClientConnection() throws IOException{
        while (true) {
            try {
                Object next = input.readObject();
                Answer answer = (Answer) next;
                processServerAnswer(answer);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(Object message) throws IOException{
        output.writeObject(message);
    }

    public void processServerAnswer(Object inputObj) throws IOException {
        if(inputObj instanceof Connection){
            if(((Connection) inputObj).isConnection()){
                view.handShake(((Connection) inputObj).getMessage());
                send(new ClientConnection("Client connected!"));
            }
        }
        else if(inputObj instanceof RequestNickname){
            String nickname = view.askNickname(((RequestNickname) inputObj).getMessage());
            send(new SendNickname(nickname));
        }
        else if(inputObj instanceof PlayersNumber){
            String number = view.askPlayerNumber(((PlayersNumber) inputObj).getMessage());
            output.writeObject(new NumberOfPlayers(number));
        }
        else if(inputObj instanceof InvalidNickname){
            String nickname = view.askNickname(((InvalidNickname)inputObj).getMessage());
            output.writeObject(new SendNickname(nickname));
        }
    }

}
