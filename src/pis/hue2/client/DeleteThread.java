package pis.hue2.client;

import javafx.application.Platform;
/**
 * Ein Objekt dieser Klasse wird erstellt, wenn eine Anfrage auf Loeschung einer Datei gestellt wird.
 */
public class DeleteThread extends RequestThread {

    private String filename;

    /**
     *  Klassenkonstruktor, welcher den Konstruktor von RequestThread aufruft
     *  und einen String erhaelt, welcher den Dateinamen der zu loeschenden Datei
     *  spezifizert.
     *
     * @param client Objekt vom Typ Client
     * @param filename spezifiert den Namen der zu loeschenden Datei
     */
    public DeleteThread(Client client, String filename) {

        super(client);
        this.filename = filename;
    }

    /**
     * Wenn der Thread gestartet wird, wird das Loeschen der spezifizierten Datei angestoßen.
     */
    public void run() {

        client.deleteFile(filename);


    }
}

