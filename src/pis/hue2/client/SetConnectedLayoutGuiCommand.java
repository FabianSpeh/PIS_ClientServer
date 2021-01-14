package pis.hue2.client;
/**
 * Ein Objekt dieser Klasse wird erstellt, wenn ein Client erfolgreich verbunden ist.
 */
public class SetConnectedLayoutGuiCommand extends GuiCommand {






    /**
     * Klassenkonstruktor, welcher den Konstruktor von GuiCommand  aufruft
     * und einen String text spezifiziert, welcher an die Konsole angehaengt wird.
     * @param controller Objekt vom Typ Controller
     */
    public SetConnectedLayoutGuiCommand(Controller controller){
        super(controller);


    }

    /**
     * Wenn der Thread gestartet wird, wird eine Methode  die die hergestellte Verbindung prueft gestartet
     * Das Layout wird angepasst
     */
    public void run(){

        controller.checkForConnection();

    }


}
