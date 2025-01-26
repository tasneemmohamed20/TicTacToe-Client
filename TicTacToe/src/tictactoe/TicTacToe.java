/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 *
 * @author tasneem
 */
public class TicTacToe extends Application {
    private static Stage primaryStage;
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("Menu.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest((event) -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to close the client?", ButtonType.YES,ButtonType.NO);
            alert.showAndWait().ifPresent(click->{
                if (click == ButtonType.YES) {
                
                Platform.exit();
                } else if (click == ButtonType.NO) {
                    event.consume(); 
                }
                
            });
        });
    }
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
