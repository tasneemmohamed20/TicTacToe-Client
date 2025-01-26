package tictactoe;

import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.RequsetModel;
import models.ResponsModel;
import models.UserModel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FXML Controller class
 */
public class MenuController implements Initializable {

    @FXML
    private Button onlineBtn;
    @FXML
    private Button guestBtn;
    @FXML
    private Button localBtn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    
 

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleLevelsNavigation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Levels.fxml"));
            Parent levelsRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene levelsScene = new Scene(levelsRoot);
            stage.setScene(levelsScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not navigate to the levels page.");
        }
    }

    @FXML
    private void handleLocalNavigation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LocalHvsH.fxml"));
            Parent levelsRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene levelsScene = new Scene(levelsRoot);
            stage.setScene(levelsScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not navigate to the Local page.");
        }
    }

    @FXML
    private void handleOnlineNavigation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Signup.fxml"));
            Parent levelsRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene levelsScene = new Scene(levelsRoot);
            stage.setScene(levelsScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not navigate to the Signup page.");
        }
    }
    @FXML
    private void navToRecords(ActionEvent event)
    {
         try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AllRecords.fxml"));
            Parent levelsRoot = loader.load();
            AllRecordsController controller = loader.getController();
            controller.setName("local");
           controller.setIsOnline(false);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene levelsScene = new Scene(levelsRoot);
            stage.setScene(levelsScene);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
