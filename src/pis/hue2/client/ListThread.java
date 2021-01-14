package pis.hue2.client;

import javafx.application.Platform;

import java.io.IOException;
import java.net.Socket;

/**
 * Ein Objekt dieser Klasse wird erstellt, wenn eine Anfrage nach einer Dateiliste gestellt wird
 */
public class ListThread extends RequestThread {

    /**
     * Klassenkonstruktor, welcher einen Client erhaelt und speichert
     * @param client Objekt vom Typ Client
     */
    public ListThread(Client client) {

        super(client);
    }

    /**
     * Wenn der Thread gestartet wird, wird das  wird sobald moeglich, die Liste der Dateien der Konsole
     * in der Client GUI hinzugefuegt.
     *
     */
    public void run(){

        Platform.runLater(new UpdateTextAreaGuiCommand(client.getController(), client.listFiles()));
    }



}



