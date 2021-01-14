package pis.hue2.client;

import javafx.application.Platform;

import java.io.IOException;
import java.net.Socket;
/**
 * Ein Objekt dieser Klasse wird erstellt, wenn eine Anfrage auf Verbindungsaufbau gestellt wird.
 */
public class ConnectThread extends RequestThread {


    /**
     * Klassenkonstruktor, welcher den Konstruktor von RequestThread aufruft
     * @param client Objekt vom Typ Client
     */
    public ConnectThread(Client client) {

        super(client);
    }
    /**
     * Wenn der Thread gestartet wird, startet der Verbindungsaufbau des Clients zum Server.
     * Außerdem wird das Layout der ClientGUI sobald wie moeglich geaendert.
     */
    public void run() {


            client.connectClient();
        Platform.runLater(new SetConnectedLayoutGuiCommand(client.getController()));




    }}