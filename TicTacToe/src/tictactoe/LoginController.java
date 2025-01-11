/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Ism
 */
public class LoginController implements Initializable {

    @FXML
    private Button easyBtn;
    @FXML
    private Button mediumBtn;
    @FXML
    private Button hardBtn;


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    public void handleSignupNavigation(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Signup.fxml"));
        Parent signupRoot = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene signupScene = new Scene(signupRoot);
        stage.setScene(signupScene);
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    public void handleLevelsNavigation(ActionEvent event) {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Levels.fxml"));
        Parent signupRoot = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene signupScene = new Scene(signupRoot);
        stage.setScene(signupScene);
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
    }

    public void handleLocalNavigation(ActionEvent event) {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Parent signupRoot = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene signupScene = new Scene(signupRoot);
        stage.setScene(signupScene);
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
    }

    @FXML
    public void handleHardModeNavigation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/robot/FXML.fxml"));
            Parent signupRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene signupScene = new Scene(signupRoot);
            stage.setScene(signupScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }

    
}
