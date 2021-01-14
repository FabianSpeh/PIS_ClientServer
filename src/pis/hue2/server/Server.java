package pis.hue2.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.OutputStream;

/**
* Die Server-Klasse hat die Aufgabe verschiedene Unterthreads
* genau so anzustossen, dass nie mehr als drei parallel laufen und
* den Start der jeweils neuen Verbindungen zu managen.
* Als Parameter wird der Port des Servers gefordert.
*/
public class Server implements Runnable{
    private ArrayList<SocketThread> threads = new ArrayList<SocketThread>();        // Hier werden alle SocketThreads hinzugefuegt
    private ServerSocket sSock = null;                                              // Hier wird der ServerSocket gespeichert
    private int port;                                                               // Hier wird der ServerPort gespeichert
    private Instruction instruction;                                                // Hier wird der Instruktionszugriff gewaehrleistet
    
    /**
     * @param port Serverport
     */
    public Server(int port){
        this.port = port;
    }

    /**
    * Der Thread startet mithilfe des ServerSocket-Objekts
    * bei validen Verbindungsumstaenden eine initiale 
    * Verbindung.
    * Im Fehlerfall wird eine DND Nachricht gesendet.
    */
    public synchronized void run(){
        try {
            this.sSock = new ServerSocket(this.port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(this.isValid()){
            this.initSocketThread();
        }
        this.sendToClient(this.instruction.DND);
    }

    /**
    * In der initSocketThread-Methode wird ein Socket
    * zum Client und damit die Verbindung hergestellt.
    * Im Anschluss dazu wird ein Objekt der Klasse SocketThread
    * erstellt und gestartet, um die weitere Kommunikation und 
    * Logik des ServerProgramms zu managen.
    * Im Fehlerfall wird eine DND Nachricht gesendet.
    */
    public synchronized void initSocketThread(){
        try {
            Socket cSock = this.sSock.accept();
            this.threads.add(new SocketThread(cSock));
            this.threads.get(this.threads.size()-1).start();
        } catch (IOException e) {
            this.sendToClient(this.instruction.DND);
            e.printStackTrace();
        }
    }

    /**
    * Hier wird zunaechst geprueft wie viele Threads noch
    * aktiv sind, um so sicherzustellen, dass nie mehr
    * als drei zur gleichen Zeit laufen.
    * Anschliessend wird der entspr. Boolean zurueckgegeben.
    * @return Wahrheitswert
    */
    public synchronized boolean isValid(){
        int i[] = {0};
        this.threads.forEach((t)->{if(t.isAlive()){i[0]++;}});
        return i[0]<3;
    }

    /**
    * Einfache Methode zur einheitlichen Kommunikation
    * /Nachrichtenversenden zum Client ueber den dafuer
    * vorgesehenen ClientSocket.
    * Die zuversendende Nachricht ist als Parameter zu uebergeben.
    * @param msg Instruktionsnachricht
    */
    public synchronized void sendToClient(Instruction msg){
        try{
          Socket cSock = this.sSock.accept();
          OutputStream out = cSock.getOutputStream();
          out.write(msg.toString().getBytes());
          out.close();
        }catch(IOException e){
          e.printStackTrace();           
        }
    }
}