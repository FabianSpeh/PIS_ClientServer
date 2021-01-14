package pis.hue2.client;

/**
 * abstrakte Klasse, welche einen allgemeinen Thread zur Veraenderung der GUI darstellt
 */
public abstract class GuiCommand implements Runnable {
Controller controller;

    /**
     * Klassenkonstruktor, welcher einen Controller erhaelt und speichert
     * @param controller Objekt vom Typ Controller
     */
    public GuiCommand(Controller controller){

    this.controller = controller;
}

}
