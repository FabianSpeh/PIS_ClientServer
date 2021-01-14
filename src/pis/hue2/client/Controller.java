package pis.hue2.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.util.Optional;

/**
 * steuert alle Interaktionen mit der Client GUI
 */
public class Controller {
@FXML
    private TextArea TextOutputArea;
@FXML
    private Button ConnectBtn;
    @FXML
    private Button DscBtn;
    @FXML
    private Button GetBtn;
    @FXML
    private Button PutBtn;
    @FXML
    private Button DelBtn;
    @FXML
    private Button LstBtn;
    @FXML
    private TextField portText;
    @FXML
    private TextField addressText;
    @FXML
    private Label portLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label connectionLabel;

    private Client client;

   private String filename;

    private Optional<String> result;

    /**
     * fuegt Text der Konsole hinzu
     * @param text spezifiziert den hinzuzufuegenden Text
     */
   public void updateTextArea(String text){

       TextOutputArea.appendText(text);
   }

    /**
     * wird beim Starten der GUI aufgerufen
     */
    public void initialize(){

       setButtonTooltip();
    }

    /**
     * deaktiviert den Connect Button
     */
   public void disableConnectBtn(){

       ConnectBtn.setDisable(true);
   }

    /**
     * aktiviert den Connect Button
     */
    public void enableConnectBtn(){

        ConnectBtn.setDisable(false);
    }

    /**
     * ueberprueft ob der Client verbunden ist
     */
    public void checkForConnection(){

        if(client.isConnected() == true){

            setConnectedLayout();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Connection established");
            alert.setHeaderText(null);
            alert.setContentText("Connection to Server was successful!");
            alert.showAndWait();
        }
        enableConnectBtn();
    }

    /**
     * ueberprueft ob der Client nicht verbunden ist
     */
    public void checkForDisconnection(){
        if(client.isConnected() == false){

            setDisconnectedLayout();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Disconnected");
            alert.setHeaderText(null);
            alert.setContentText("Successfully disconnected from Server!");
            alert.showAndWait();
        }

    }


    /**
     * wird ausgefuehrt wenn der LST Button gedrueckt wird
     * @param event LST
     */
    public void pressListBtn(ActionEvent event){

       disableAllBtn();
       ListThread t = new ListThread(client);
       t.start();
       enableAllBtn();


    }

    /**
     * wird ausgefuehrt wenn der Connect Button gedrueckt wird
     * @param event CON
     */

    public void pressConnectBtn(ActionEvent event) {
try {
    disableConnectBtn();
    if (portText.getText().equals("") && (addressText.getText().equals(""))) {

        client = new Client();
    } else client = new Client(addressText.getText(), Integer.parseInt(portText.getText()));
    client.setController(this);
    ConnectThread t = new ConnectThread(client);
    t.start();


    enableConnectBtn();
}catch(Exception e){
    Platform.runLater(new UpdateTextAreaGuiCommand(this,"Client couldn't connect to the specified server.\n"));
    e.printStackTrace();
    enableConnectBtn();

}

    }


    /**
     * wird ausgefuehrt wenn der DSC Button gedrueckt wird
     * @param event DSC
     */
    public void pressDscButton(ActionEvent event){


    DisconnectThread t = new DisconnectThread(client);
    t.start();

    }

    /**
     * wird ausgefuehrt wenn der DEL Button gedrueckt wird
     * @param event DEL
     */
    public void pressDelButton(ActionEvent event){
       disableAllBtn();
        createTextInputDialog("<filename>","specify file to delete");

        if(result.isPresent()){

            filename = result.get();
            DeleteThread t = new DeleteThread(client,filename);
            t.start();
            enableAllBtn();
        }

        else{

            enableAllBtn();
            return;

        }

    }

    /**
     * wird ausgefuehrt wenn der PUT Button gedrueckt wird
     * @param event PUT
     */
    public void pressPutButton(ActionEvent event){
       disableAllBtn();
        createTextInputDialog("<filename>","specify file to upload");

        if(result.isPresent()){

            filename = result.get();
            PutThread t = new PutThread(client,filename);
            t.start();
            enableAllBtn();
        }

        else{

            enableAllBtn();
            return;

        }

    }

    /**
     * wird ausgefuehrt wenn der GET Button gedrueckt wird
     * @param event GET
     */
    public void pressGetButton(ActionEvent event){
       disableAllBtn();
       createTextInputDialog("<filename>","specify file to download");

        if(result.isPresent()){

            filename = result.get();
            GetThread t = new GetThread(client,filename);
            t.start();
            enableAllBtn();
        }

        else{

            enableAllBtn();
            return;

        }

    }


private void setConnectedLayout(){

        ConnectBtn.setVisible(false);
    portLabel.setVisible(false);
    portText.setVisible(false);
    addressLabel.setVisible(false);
    addressText.setVisible(false);
    connectionLabel.setText("connected to: " + client.getAddress().toString());
    LstBtn.setVisible(true);
    DscBtn.setVisible(true);
    GetBtn.setVisible(true);
    PutBtn.setVisible(true);
    DelBtn.setVisible(true);
    TextOutputArea.setVisible(true);
}

private void setDisconnectedLayout(){

    ConnectBtn.setVisible(true);
    portLabel.setVisible(true);
    portText.setVisible(true);
    addressLabel.setVisible(true);
    addressText.setVisible(true);
    connectionLabel.setText("");
    LstBtn.setVisible(false);
    DscBtn.setVisible(false);
    GetBtn.setVisible(false);
    PutBtn.setVisible(false);
    DelBtn.setVisible(false);
    TextOutputArea.setVisible(true);

}

private void disableAllBtn(){
       LstBtn.setDisable(true);
    GetBtn.setDisable(true);
    DscBtn.setDisable(true);
    PutBtn.setDisable(true);
    DelBtn.setDisable(true);




}

    private void enableAllBtn(){
        LstBtn.setDisable(false);
        GetBtn.setDisable(false);
        DscBtn.setDisable(false);
        PutBtn.setDisable(false);
        DelBtn.setDisable(false);




    }

    private void createTextInputDialog(String prompt, String header){
       TextInputDialog td = new TextInputDialog(prompt);
       td.setHeaderText(header);
        result =  td.showAndWait();




    }

    private void setButtonTooltip(){

       GetBtn.setTooltip(new Tooltip("downloads a specified file from the Server"));
       PutBtn.setTooltip(new Tooltip("uploads a specified file to the Server"));
       LstBtn.setTooltip(new Tooltip("lists the available files on the Server"));
       DelBtn.setTooltip(new Tooltip("deletes a specified file on the Server"));
       DscBtn.setTooltip(new Tooltip("disconnects the Client from the Server"));
    }


}
