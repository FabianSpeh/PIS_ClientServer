package pis.hue2.client;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Ein Objekt dieser Klasse wird erstellt, wenn eine Veränderung der Textarea (Konsole) in der GUI
 * vorgenommen wird.
 */
public class UpdateTextAreaGuiCommand extends GuiCommand {
    private String text;


    /**
     * Klassenkonstruktor, welcher den Konstruktor von GuiCommand  aufruft
     * und einen String text spezifiziert, welcher an die Konsole angehaengt wird.
     * @param controller Objekt vom Typ Controller
     * @param text spezifiert den Text, welcher hinzugefuegt werden soll
     */
    public UpdateTextAreaGuiCommand(Controller controller,String text){
        super(controller);
        this.text = text;

    }

    /**
     * Wenn der Thread gestartet wird, wird eine Methode,
     * die für das Veraendern der Konsole zuständig ist gestartet
     */
    public void run(){

        controller.updateTextArea(text);

    }


}
