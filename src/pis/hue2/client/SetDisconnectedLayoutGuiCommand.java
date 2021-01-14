package pis.hue2.client;
/**
 * Ein Objekt dieser Klasse wird erstellt, wenn ein Client erfolgreich die Verbindung abgebrochen hat.
 */
public class SetDisconnectedLayoutGuiCommand extends GuiCommand {

    /**
     * Klassenkonstruktor, welcher den Konstruktor von GuiCommand  aufruft
     * und einen String text spezifiziert, welcher an die Konsole angehaengt wird.
     * @param controller Objekt vom Typ Controller
     */
    public SetDisconnectedLayoutGuiCommand(Controller controller){
        super(controller);


    }
    /**
     * Wenn der Thread gestartet wird, wird eine Methode, die die abgebrochene Verbindung prueft gestartet
     * Das Layout wird angepasst
     */
    public void run(){

        controller.checkForDisconnection();

    }


}
