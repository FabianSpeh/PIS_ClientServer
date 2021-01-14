package pis.hue2.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * ist für das Starten der GUI (Clients) zuständig
 */
public class launchClient extends Application {


    /**
     * oeffnet für jeden Client eine eigene Stage
     * @param primaryStage spezifiziert die primaere Stage
     * @throws Exception ,wenn Stage nicht aufgebaut wird
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
       /* Parent root2 = FXMLLoader.load(getClass().getResource("sample2.fxml"));
        Parent root3 = FXMLLoader.load(getClass().getResource("sample3.fxml"));
        Parent root4 = FXMLLoader.load(getClass().getResource("sample4.fxml"));*/

        primaryStage.setTitle("Client");
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();

       /* Stage secondaryStage = new Stage();
        secondaryStage.setTitle("Client2");
       secondaryStage.setScene(new Scene(root2, 500, 500));
       secondaryStage.show();

       Stage thirdStage = new Stage();
        thirdStage.setTitle("Client3");
        thirdStage.setScene(new Scene(root3, 500, 500));
        thirdStage.show();

        Stage fourthStage = new Stage();
        fourthStage.setTitle("Client4");
       fourthStage.setScene(new Scene(root4, 500, 500));
       fourthStage.show();*/
    }

    /**
     * In der main-Methode wird die start() Methode angestoßen
     * @param args String[] args
     */
    public static void main(String[] args) {
        launch(args);
    }


}
