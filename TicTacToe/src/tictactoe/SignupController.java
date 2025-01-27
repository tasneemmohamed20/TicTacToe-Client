package tictactoe;

import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.RequsetModel;
import models.ResponsModel;
import models.UserModel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

public class SignupController {

    @FXML
    private Button signupBTN;
    @FXML
    private Button loginNav;
    @FXML
    private TextField usernameTF;
    @FXML
    private TextField passTF;

    @FXML
    public void handleSignup(ActionEvent event) {
        try {
            String username = usernameTF.getText().trim();
            String password = passTF.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Username and password cannot be empty.");
                return;
            }

            PlayerSocket playerSocket = PlayerSocket.getInstance();
            DataOutputStream dos = playerSocket.getDataOutputStream();
            DataInputStream dis = playerSocket.getDataInputStream();

            UserModel user = new UserModel(0, username, password, "0", "offline");
            RequsetModel request = new RequsetModel("register", user);

            String jsonRequest = new Gson().toJson(request);
            dos.writeUTF(jsonRequest);
            dos.flush();

            String jsonResponse = dis.readUTF();
            ResponsModel response = new Gson().fromJson(jsonResponse, ResponsModel.class);

            if ("success".equals(response.getStatus())) {
                showAlert(Alert.AlertType.INFORMATION, "Signup Successful", response.getMessage());
                clearFields();
                navigateToLogin(event);
            } else {
                showAlert(Alert.AlertType.ERROR, "Signup Failed", response.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Connection Error", "Failed to connect to the server: " + e.getMessage());
        }
    }

    private void clearFields() {
        usernameTF.clear();
        passTF.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
    Stage alertStage = new Stage();
    alertStage.initModality(Modality.APPLICATION_MODAL);
    alertStage.setTitle(title);

    VBox alertBox = new VBox(15);
    alertBox.setAlignment(Pos.CENTER);
   // alertBox.getStyleClass().add("custom-alert-box");

    javafx.scene.control.Label messageLabel = new javafx.scene.control.Label(message);
   // messageLabel.getStyleClass().add("custom-alert-label");

    Button okButton = new Button("OK");
    okButton.getStyleClass().add("custom-alert-button");
    okButton.setOnAction(e -> alertStage.close());

    alertBox.getChildren().addAll(messageLabel, okButton);

    Scene scene = new Scene(alertBox, 300, 150);
    scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
    alertStage.setScene(scene);
    alertStage.showAndWait();
}


    @FXML
    private void handleLoginNavigation(ActionEvent event) {
        navigateToLogin(event);
    }

    private void navigateToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent loginRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene loginScene = new Scene(loginRoot);
            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not navigate to the login page.");
        }
    }
    
    @FXML
    private void handleBackButton(ActionEvent event) {
        new LoginController().navigateToScreen(event, "Menu.fxml", "Menu");
    }
}