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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Ism
 */
public class LevelsController implements Initializable {

    @FXML
    private Button easyBtn;
    @FXML
    private Button hardBtn;
    @FXML
    private Button backButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    public void handleEasyModeNavigation(ActionEvent event) {
        navigateToScreen(event, "/EasyMode/EasyMode.fxml", "Easy Mode");
    }
    @FXML
    public void handleHardModeNavigation(ActionEvent event) {
        navigateToScreen(event, "/robot/FXML.fxml", "Hard Mode");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private FXMLLoader navigateToScreen(ActionEvent event, String fxml, String title) {
        FXMLLoader loader = null;
        try {
            URL fxmlLocation = getClass().getResource(fxml);
            if (fxmlLocation == null) {
                throw new IOException("Can't find this file " + fxml);
            }
            loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", " Could not navigate to " + title + " screen.\nCheck if the FXML file is in the correct folder.");
        }
        return loader;
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        navigateToScreen(event, "Menu.fxml", "Menu");
    }
    
    

}
