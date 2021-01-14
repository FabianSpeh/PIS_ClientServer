package pis.hue2.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * abstrakte Klasse, welche einen allgemeinen Anfragethread darstellt.
 */
public abstract class RequestThread extends Thread{
     Client client;

    /**
     * Klassenkonstruktor, welcher einen Client erhaelt und speichert
     * @param client Objekt vom Typ Client
     */
    public RequestThread(Client client) {

    this.client = client;
}




    }
