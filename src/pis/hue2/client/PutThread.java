package pis.hue2.client;


/**
 * Ein Objekt dieser Klasse wird erstellt, wenn eine Anfrage auf Upload einer Datei gestellt wird
  */
public class PutThread extends RequestThread {

    private String filename;

    /**
     *Klassenkonstruktor, welcher den Konstruktor von RequestThread aufruft
     * und einen String erhaelt, welcher den Namen der Datei, welche hochgeladen werden soll spezifiziert
     * @param client Objekt vom Typ Client
     * @param filename spezifiert den Namen der Datei, welche hochgeladen werden soll
     */
    public PutThread(Client client, String filename){

        super(client);
        this.filename = filename;
    }

    /**
     * Wenn der Thread gestartet wird, wird das Hochladen der spezifizierten Datei angestoßen.
     */
    public void run(){

        client.putFile(filename);


    }


}
