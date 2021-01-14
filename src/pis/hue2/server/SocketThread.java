package pis.hue2.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import java.nio.file.Path;
import java.net.SocketException;
import java.nio.file.Paths;
import java.lang.IllegalArgumentException;
import java.nio.file.Files;

/**
* Die SocketThread-Klasse ist der logische Endpunkt
* mit dem der Client im Endeffekt verbunden wird und
* wo die Dateitransferaktionen stattfinden.
* Uebergeben wird der clientSocket zur Kommunikation.
*/
public class SocketThread extends Thread{
    private Instruction instruction;            // Hier wird der Instruktionszugriff gewaehrleistet
    private DataOutputStream out = null;            // Hierueber wird die Ausgabe gewaehrleistet
    private DataInputStream in = null;              // Hierueber wird das Einlesen ermoeglicht
    private Socket cSock;                       // ClientSocket fuer In-/Output
    private String path;                        // Pfad zu den zu verwaltenden Dateien
    private boolean isCon;                      // Status der Konnektierung
    private String[] command;      // Argumente der Anweisung von Client

    /**
     * @param cSock Socket zum Client
     */
    public SocketThread(Socket cSock) {
        try{
            this.cSock = cSock;
            this.in = new DataInputStream(cSock.getInputStream());
            this.out = new DataOutputStream(cSock.getOutputStream());
            this.path = Paths.get("D:\\Desktop\\PIS HUE2 IntelliJ\\src\\pis\\hue2\\server\\files").toString();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
    * Solange die Verbindung nicht (client- oder serverseitig) gecancelt wird
    * wird jeweils die neue Instruktion vom Client und die damit verbundene
    * Funktion abgerufen.
    */
    public void run() {
      while(this.getAndExecuteInstruction()!=this.instruction.DSC);
    }

    /**
    * Hier wird die Instruktion vom Client empfangen und geparst,
    * um dann die entsprechende Funktion aufzurufen, die die
    * gewuenschte Aktion ausfuehrt.
    * Nach dem die initiale Konnektierung mit CON sichergestellt wurde,
    * wird nur noch via Switch-Case im vorgegebenen InstructionEnumerate
    * die "Zuweisung" zu den auszufuehrenden Aktionen gemacht.
    * Im default/Fall falscher Eingabe wird ein DND zurueckgegeben.
    * @return Instruktionsanweisung fuer while-Schleife
    */
    public synchronized Instruction getAndExecuteInstruction(){
      Instruction instructedFile = null;
      try{
        this.command = this.getFromClient().split(" ");
        instructedFile = this.instruction.valueOf(command[0]);
      }catch(IllegalArgumentException e){
        return this.instruction.DSC;
      }
      if (instructedFile==this.instruction.CON){
        this.isCon=true;
        this.sendToClient(this.instruction.ACK);
        return instructedFile;
      }
      if (this.isCon){
        switch(instructedFile){
          case LST:
            this.listFiles();
            break;
          case PUT:
            System.out.println("PUT started");
            this.putFile();
            break;
          case GET:
            this.sendFileToClient();
            break;
          case DEL:

            this.deleteFile();
            break;
          case DSC:
            this.disconnect();
            break;
          default:
            this.sendToClient(this.instruction.DND);
            break;
        }
      }
      return this.instruction.DND;
    }

      /**
      * Hier wird die aufgebaute Verbindung und alle
      * damit verbundenen In- und Outputkanaele geschlossen
      * und mit der Nachricht DSC bestaetigt.
      */
      public synchronized void disconnect(){
        this.sendToClient(this.instruction.DSC);
        this.isCon = false;
        try{
          this.out.close();
          this.in.close();
          this.cSock.close();
        } catch(IOException e){
            e.printStackTrace();
        }
      }

      /**
      * Hier wird das einheitliche Versenden zum Client
      * uebernommen. Zu uebergeben ist die Nachricht msg.
      * @param msg Instruktionsnachricht
      */
      public synchronized void sendToClient(Instruction msg){

        try{
          this.out.write((msg.toString()+"\n").getBytes());
        }catch(IOException e){
          e.printStackTrace();           
        }
      }

      /**
      * Hier wird das einheitliche Empfangen vom Client
      * definiert. 
      * Zurueckgegeben wird als String;
      * Im Fehlerfall wird ein Leerstring returned.
      * @return geparster String
      */ 
      public synchronized String getFromClient(){
        try{
          BufferedReader in = new BufferedReader(new InputStreamReader(this.in));
          return in.readLine().toString();
        }catch(SocketException e){ 
          return "";
        }catch(IOException e){
          return "";
        }
      }

      /**
      * Die Methode gibt eine kommaseparierte Liste
      * aller Dateien im path-Verzeichnis wieder.
      * Ueber die Dateien wird mit einer for-Schleife
      * iterierte, der zu returnende String wird
      * via StringBuilder erstellt.
      * Im Fehlerfall wird ein DND an den Client erwidert.
      */
      public synchronized void listFiles(){
        this.sendToClient(this.instruction.ACK);
        if (!this.getFromClient().equals("ACK")){
          this.sendToClient(this.instruction.DND);
          return;
        }
        try{
          File[] dir = new File(this.path).listFiles();   
          StringBuilder filenames = new StringBuilder("");
          for (int i = 0; i < dir.length; i++) {
            filenames.append(dir[i].getName().trim()+",");
          }
          filenames.deleteCharAt(filenames.lastIndexOf(","));
          String tFilename = filenames.toString();
          //this.out.write((this.instruction.DAT.toString() + " " + tFilename.length() + " " + tFilename.getBytes().toString() + "\n").getBytes());

          byte [] bytes = tFilename.getBytes();
          long length = bytes.length;

          this.out.write(this.instruction.DAT.toString().getBytes());
          this.out.writeLong(length);
          this.out.write(bytes);
          this.out.write("\n".getBytes());
          this.out.flush();

        }catch(IOException e){
          this.sendToClient(this.instruction.DND);
          e.printStackTrace(); 
        }
        if(this.getFromClient().equals("ACK")){

        }
      }

      /**
      * Die Methode wartet auf Zeilenweisen Erhalt der Datei.
      * Diese wird dann, sofern noch nicht existent, erstellt 
      * und bis zum Ende der Uebermittlung, das durch EOF signalisiert wird,
      * mit den Inhalten beschrieben.
      * Im Fehlerfall wird mit DND geantwortet.
      */
      public synchronized void putFile(){
        try{
        this.sendToClient(this.instruction.ACK);
        byte[] instructionBytes = new byte[]{in.readByte(),in.readByte(),in.readByte()};
        String instruction = new String(instructionBytes, "UTF-8");


        if(instruction.equals("DAT")){
        Path path_new = Paths.get("D:\\Desktop\\PIS HUE2 IntelliJ\\src\\pis\\hue2\\server\\files");
          long length = in.readLong();
          byte[] fileContent = new byte[(int)length];
          in.read(fileContent);
          FileOutputStream fos = new FileOutputStream(path_new.resolve(this.command[1]).toFile());
          fos.write(fileContent);
          this.sendToClient(this.instruction.ACK);
          in.readLine();


        }


      }
        catch(IOException e){
          this.sendToClient(this.instruction.DND);
          e.printStackTrace();

        }}

      /**
      * Hier wird das versenden von Dateien zum Client geregelt.
      * der Pfad der angeforderten Datei wird analog zum Loeschvorgang
      * zusammengebaut, im Anschluss die Existenz geprueft, mit ACK
      * bestaetigt und nach erhaltenem ACK und versendeten DAT wird die
      * Datei als Bytestrom zum Client gesendet.
      * Anschliessend bestaetigt der Client mit ACK. Im Fehlerfall
      * wird mit DND geantwortet.
      */
      public synchronized void sendFileToClient(){

        File f = new File(this.path, this.command[1]);
        if (f.exists()){
          this.sendToClient(this.instruction.ACK);
          if (this.getFromClient().equals("ACK")){
            // Transfer of bytes
            try{
              byte [] bytes = Files.readAllBytes(Paths.get(this.path, this.command[1]));
              long length = bytes.length;

              //this.out.write((this.instruction.DAT.toString() + " " + bytes.length + " " + bytes.toString() + "\n").getBytes());

              this.out.write(this.instruction.DAT.toString().getBytes());
              this.out.writeLong(length);
              this.out.write(bytes);
              this.out.write("\n".getBytes());
              this.out.flush();

            }catch(Exception e){
              e.printStackTrace();
              this.sendToClient(this.instruction.DND);
            }
            if(this.getFromClient().equals("ACK")){

              return;
            };
            this.sendToClient(this.instruction.DND);
            return;
          }
        }
        this.sendToClient(this.instruction.DND);
      }

      /**
      * Hier wird das Loeschen von Dateien ermoeglicht.
      * Der Pfad der Datei wird aus path-Variable und ClientInput
      * zusammengebastelt.
      * Bei Erfolg wird mit ACK, bei Fehler mit DND
      * geantwortet.
      */
      public synchronized void deleteFile(){

        File instructedFile = new File(this.path, this.command[1]);
        if (!instructedFile.exists()){
          this.sendToClient(this.instruction.DND);
          return;
        }
        if (instructedFile.delete()){   //make flexible
          this.sendToClient(this.instruction.ACK);
        }else{
          this.sendToClient(this.instruction.DND); 
        }
      }





}