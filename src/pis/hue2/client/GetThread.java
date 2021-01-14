package pis.hue2.client;

import javafx.application.Platform;

/**
 * Ein Objekt dieser Klasse wird erstellt, wenn eine Anfrage auf Download einer Datei gestellt wird
 */
public class GetThread extends RequestThread {

   private String filename;

    /**
     * Klassenkonstruktor, welcher den Konstruktor von RequestThread aufruft
     * und einen String erhaelt, welcher den Namen der Datei, welche heruntergeladen werden soll spezifiziert
     * @param client Objekt vom Typ Client
     * @param filename spezifiert den Namen der Datei, welche heruntergeladen werden soll
     */
    public GetThread(Client client, String filename) {

        super(client);
        this.filename = filename;
    }

    /**
     * Wenn der Thread gestartet wird, wird das Herunterladen der spezifizierten Datei angestoßen.
     */
    public void run() {

        client.getFile(filename);


    }
}
