package pis.hue2.client;

import javafx.application.Platform;
/**
 * Ein Objekt dieser Klasse wird erstellt, wenn eine Anfrage auf Verbindungsabbruch  gestellt wird
 */
public class DisconnectThread extends RequestThread{


    /**
     * Klassenkonstruktor, welcher den Konstruktor von RequestThread aufruft
     * @param client Objekt vom Typ Client
     */
    public DisconnectThread(Client client) {

        super(client);
    }

    /**
     * Wenn der Thread gestartet wird, startet der Verbindungsabbruch zum Server
     * Außerdem wird das Layout der ClientGUI sobald wie moeglich geaendert.
     */
    public void run() {

        client.disconnectClient();
        Platform.runLater(new SetDisconnectedLayoutGuiCommand(client.getController()));



    }}

