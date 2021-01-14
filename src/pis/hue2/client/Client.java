package pis.hue2.client;

import javafx.application.Platform;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Die Client-Klasse symbolisiert den Client, welcher dazu in der Lage ist
 * sich mit einem Server zu verbinden und verschiedenen Dateitransferaktionen
 * anzustossen.
 */
public class Client {


    private InetSocketAddress address;
private Socket socket;
private Instruction instruction;
private DataOutputStream output;
private DataInputStream input;
private boolean connected = false;
private Controller controller;
Path folder = Paths.get("D:\\Desktop\\PIS HUE2 IntelliJ\\src\\pis\\hue2\\client\\files\\");






    /**
     * Klassenkonstruktor, welcher eine Default Server-Adresse erstellt.
     */

    public Client(){


     address = new InetSocketAddress("127.0.0.1",3000);
    }

    /**
     * Klassenkonstruktor, welcher es erlaubt die Host-Adresse und den Port zu spezifizieren.
     * @param hostname spezifiziert Host-Adresse
     * @param port spezifiziert Port
     */

    public Client(String hostname, int port){

        address = new InetSocketAddress(hostname,port);
    }

    /**
     * @return gibt ein Objekt vom Typ InetSocketAddress zurueck, welches den hostname und port enthaelt
     * mit dem der Socket einen Verbindungsaufbau startet.
     */

    public InetSocketAddress getAddress() {
        return address;
    }

    /**
     * @return gibt ein Objekt vom Typ Controller zurueck, welcher für die Steuerung der GUI verantwortlich ist.
     */

    public Controller getController() {
        return controller;
    }

    /**
     * setzt die Variable controller, welche für die Steuerung der GUI verantwortlich ist
     * @param controller spezifiziert welcher Controller als controller gesetzt werden soll.
     */

    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * @return gibt den Wert der boolean Variable connected zurueck
     */

    public boolean isConnected() {
        return connected;
    }

//
    private String getMessage(){

        String msg = "";
try {

    InputStream i = socket.getInputStream();
  InputStreamReader in = new InputStreamReader(i,"UTF-8");
    BufferedReader bf = new BufferedReader(in);

           msg = bf.readLine();

}
catch(Exception e){

    e.printStackTrace();
}

//appends message to TextArea(console)
Platform.runLater(new UpdateTextAreaGuiCommand(this.getController(),msg +"\n"));
    return msg;
    }


    private static String instructiontoString(Instruction instruction){


      String msg = "" + instruction;

      return msg;


    }

    /**
     * Die Methode erstellt einen Socket, welcher versucht mit der bei der Initalisierung
     * erstellten Adresse mit einem Serversocket zu verbinden.
     * Ausserdem erstellt sie einen Outputstream und einen Inputstream, ueber den Nachrichten
     * versendet bzw eingelesen werden koennen.
     * Der Client sendet eine Verbindungsanfrage (CON), welche entweder angenommen (ACK) oder abgelehnt wird(DND)
     */
    public void connectClient(){
        try {
            socket = new Socket();
            socket.connect(address, 5000);
            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());

            String conMsg = instructiontoString(Instruction.CON) +"\n";
           this.output.write(conMsg.getBytes("UTF-8"));
           output.flush();


            String msg = getMessage();


            if(msg.equals("ACK")){
                connected = true;

                Platform.runLater(new UpdateTextAreaGuiCommand(this.getController(),"Client connected successfully.\n"));
            }

            else if(msg.equals("DND"))  Platform.runLater(new UpdateTextAreaGuiCommand(this.getController(),"Client couldn't connect to the specified server."));

        }

        catch(IOException e){
            Platform.runLater(new UpdateTextAreaGuiCommand(this.getController(),"Client couldn't connect to the specified server."));
            e.printStackTrace();
        }




    }

    /**
     * Diese Methode sendet eine Anfrage auf Verbindungsabbruch.
     * Wenn der Server DSC zuruecksendet werden alle Datenkanaele geschlossen
     * und der zugehoerige Socket geschlossen
     */
    public void disconnectClient(){
try {
    String dscMsg = instructiontoString(Instruction.DSC) +"\n";
    this.output.write(dscMsg.getBytes("UTF-8"));
    output.flush();

    String msg = getMessage();

    if(msg.equals("DSC")){

            input.close();
            output.close();
            socket.close();

        connected = false;

        Platform.runLater(new UpdateTextAreaGuiCommand(this.getController(),"Client disconnected successfully\n"));




        }

    }

catch(Exception e){
    e.printStackTrace();
}

}

    /**
     * Diese Methode sendet eine Anfrage auf Erhalt einer Dateiliste(LST).
     * Im Erfolgsfall wird ein ACK vom Server zurueckgesendet, welches vom Client mit
     * einem ACK erwidert wird.
     * Der Server erwidert dies mit einem DAT und den angefragten Daten.
     * Im Erfolgsfall sendet der Client abschliessend ein ACK zurueck.
     * Im Fehlerfall erhaelt der Client ein DND vom Server
     * @return gibt eine formatierte Liste aller verfuegbaren Dateien auf dem Server zurueck
     */
    public String listFiles(){


        try {
            String lstMsg = instructiontoString(Instruction.LST) + "\n";
            this.output.write(lstMsg.getBytes("UTF-8"));
            output.flush();

            String msg = getMessage();

            if(msg.equals("ACK")){
                //send ACK if ACK comes back
                this.output.write((instructiontoString(Instruction.ACK)+"\n").getBytes("UTF-8"));
                this.output.flush();



                byte[] instructionBytes = new byte[]{input.readByte(),input.readByte(),input.readByte()};
                String instruction = new String(instructionBytes, "UTF-8");

                if(instruction.equals("DAT")){

                    long length = input.readLong();
                    //reads byte[] content
                    byte[] ListContent = new byte[(int)length];
                    input.read(ListContent);

                    String listMsg = new String(ListContent, "UTF-8");

                    //formats list by replacing , with \n
                    String formatted_msg = "list of files:\n" + listMsg.replace(",","\n") + "\n";
                    this.output.write((instructiontoString(Instruction.ACK)+"\n").getBytes("UTF-8"));
                    this.output.flush();
                    input.readLine();
                    Platform.runLater(new UpdateTextAreaGuiCommand(this.getController(),instruction + " " + length + " " + ListContent.toString() + "\n"));
                    return formatted_msg;
                }




        }
        }
        catch(IOException e){
            e.printStackTrace();
        }
             return null;
    }


    /**
     * Diese Methode sendet eine Anfrage auf Loeschung(DEL) einer Datei auf dem Server.
     * Wenn der Server ein ACK zuruecksendet wurde die Loeschung erfolgreich ausgefuehrt
     * Im Fehlerfall erhaelt der Client ein DND vom Server.
     *
     * @param filename spezifiziert die Datei, die auf dem Server geloescht werden soll
     */
    public void deleteFile(String filename){
        try {
            String delMsg = instructiontoString(Instruction.DEL) + " " + filename +"\n";
            this.output.write(delMsg.getBytes("UTF-8"));
            output.flush();
            String msg = getMessage();

            if(msg.equals("ACK")){

                Platform.runLater(new UpdateTextAreaGuiCommand(this.getController(),"successfully deleted " +filename +"\n"));

            }

           else if(msg.equals("DND")){

                Platform.runLater(new UpdateTextAreaGuiCommand(this.getController(),"failed to delete " +filename + " (check for spelling errors)"));

            }

        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Diese Methode sendet eine Anfrage auf Download einer Datei vom Server (GET).
     * Wenn der Server ein ACK zuruecksendet, erwidert der Client mit einem ACK.
     * Wenn der Server ein DAT mit den angefragten Daten zuruecksendet liest der Client diese aus
     * und erstellt eine Datei mit der erhaltenen Groeße und schreibt den Inhalt in diese hinein.
     * Wenn der Server ein DND schickt wird die Anfrage abgebrochen
     * @param filename spezifiziert die Datei die vom Server heruntergeladen werden soll.
     */
    public void getFile(String filename){
        //usage: GET filename:string

        //String downloadFolder = "D:\\Desktop\\PIS HUE2 IntelliJ\\src\\pis\\hue2\\client\\files";

        try{
            //send GET + filename
            String getMsg = instructiontoString(Instruction.GET) + " " + filename +"\n";

            this.output.write(getMsg.getBytes("UTF-8"));
            this.output.flush();
            String msg = getMessage();

            if(msg.equals("ACK")){

                //send ACK if ACK comes back
               this.output.write((instructiontoString(Instruction.ACK)+"\n").getBytes("UTF-8"));
               this.output.flush();

               //reads first 3 Bytes, which are the Instruction DAT
               byte[] instructionBytes = new byte[]{input.readByte(),input.readByte(),input.readByte()};
               String instruction = new String(instructionBytes, "UTF-8");



               if(instruction.equals("DAT")){


                    //reads length of byte[]
                   long length = input.readLong();

                   //reads byte[] content
                   byte[] fileContent = new byte[(int)length];

                   //reads rest of message, which should be file Content
                   input.read(fileContent);
                   createFileFromBytes(filename,fileContent);
                   this.output.write((instructiontoString(Instruction.ACK)+"\n").getBytes("UTF-8"));
                   output.flush();
                   input.readLine();
                   Platform.runLater(new UpdateTextAreaGuiCommand(this.getController(),instruction + " " + length + " " + fileContent.toString() + "\n"));
                   Platform.runLater(new UpdateTextAreaGuiCommand(this.getController(),filename + " was successfully downloaded.\n"));

               }
            }

            if(msg.equals("DND")){
                Platform.runLater(new UpdateTextAreaGuiCommand(this.getController(),"failed to download " + filename +"\n"));
                return;

            }






        }

        catch(Exception e){

            e.printStackTrace();
        }

    }

    /**
     * Diese Methode sendet eine Anfrage auf Upload einer Datei auf den Server. (PUT)
     * Wenn der Server ein ACK zuruecksendet, erwidert der Client meinem DAT und den Daten,
     * welche zur Erstellung der Datei benoetigt werden.
     * Wenn der Server anschließend ein ACK zuruecksendet war der Upload erfolgreich.
     * Wenn der Server ein DND sendet, wird die Anfrage abgebrochen.
     * @param filename spezifiziert die Datei die auf den Server hochgeladen werden soll.
     */
    public void putFile(String filename){
        try {
            String putMsg = instructiontoString(Instruction.PUT) + " " + filename +"\n";
            File f = new File(folder + "\\" + filename);

            if(f.exists()) {
                this.output.write(putMsg.getBytes("UTF-8"));
                this.output.flush();

                String msg = getMessage();

                if (msg.equals("ACK")) {

                    output.write(instructiontoString(Instruction.DAT).getBytes("UTF-8"));

                    byte[] fileContent = Files.readAllBytes(Paths.get("D:\\Desktop\\PIS HUE2 IntelliJ\\src\\pis\\hue2\\client\\files\\" + filename));

                    long length = fileContent.length;
                    output.writeLong(length);
                    output.write(fileContent);
                    output.writeBytes("\n");
                    output.flush();

                    msg = getMessage();

                    if(msg.equals("ACK")){

                        Platform.runLater(new UpdateTextAreaGuiCommand(this.getController(), filename + " was successfully uploaded to the Server!\n"));
                    }

                }


                if (msg.equals("DND")) {


                }

            }

            else Platform.runLater(new UpdateTextAreaGuiCommand(this.getController(), "'" + filename + "'"  + " doesnt exist!\n"));


        }
        catch(NoSuchFileException e ){

            e.printStackTrace();

        }
        catch(IOException e){

            e.printStackTrace();
        }
    }







    private void createFileFromBytes(String filename,byte[] bytes){
        //creates FileOutputStream corresponding to the specified file Path
        try(FileOutputStream fos = new FileOutputStream(folder.resolve(filename).toFile())){

        //writes filecontent into the File
            fos.write(bytes);


        }

        catch(Exception e){

            e.printStackTrace();
        }


    }






}

