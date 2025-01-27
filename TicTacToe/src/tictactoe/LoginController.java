package tictactoe;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.RequsetModel;
import models.ResponsModel;
import models.UserModel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

public class LoginController {
    @FXML
    private TextField usernameTF;
    @FXML
    private PasswordField passTF;
    String score;
    Gson gson = new Gson();
    @FXML
    public void handleSignupNavigation(ActionEvent event) {
        navigateToScreen(event, "Signup.fxml", "Signup");
    }
    public void handleHardmodeNavigation(ActionEvent event) {
        navigateToScreen(event, "FXML.fxml", "Hard");
    }
    public void handleEasyModeNavigation(ActionEvent event) {
        navigateToScreen(event, "D:\\java labs\\xoProject\\TicTacToe-Client\\TicTacToe\\src\\EasyMode\\EasyMode.fxml", "Easy Mode");
    }
   
    @FXML
    public void handleLogin(ActionEvent event) {
        try {
            String username = usernameTF.getText().trim();
            String password = passTF.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Username and password cannot be empty.");
                return;
            }

            UserModel user = new UserModel(0, username, password, "0", "online");
            RequsetModel request = new RequsetModel("login", user);

            ResponsModel response = sendRequest(request);

            if ("success".equals(response.getStatus())) {
                showAlert(Alert.AlertType.INFORMATION, "Login Successful", response.getMessage());
                Type userModelType = new TypeToken<UserModel>() {
                }.getType();
                UserModel data = gson.fromJson(gson.toJson(response.getData()), userModelType);

                System.out.println("data" + data.getScore());
                FXMLLoader loader = navigateToScreen(event, "dashboard.fxml", "Dashboard");
                DashboardController dashboardController = loader.getController();
                dashboardController.setScore(data.getScore());
                dashboardController.setName(data.getUserName());
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", response.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Connection Error", "Failed to connect to the server: " + e.getMessage());
        }
    }


    private ResponsModel sendRequest(RequsetModel request) throws IOException {
        PlayerSocket playerSocket = PlayerSocket.getInstance();
        DataOutputStream dos = playerSocket.getDataOutputStream();
        DataInputStream dis = playerSocket.getDataInputStream();

        String jsonRequest = new Gson().toJson(request);
        dos.writeUTF(jsonRequest);
        dos.flush();

        String jsonResponse = dis.readUTF();
        return new Gson().fromJson(jsonResponse, ResponsModel.class);
    }


   private void showAlert(Alert.AlertType alertType, String title, String message) {
    Stage alertStage = new Stage();
    alertStage.initModality(Modality.APPLICATION_MODAL);
    alertStage.setTitle(title);

    VBox alertBox = new VBox(15);
    alertBox.setAlignment(Pos.CENTER);
    alertBox.getStyleClass().add("alert");

    javafx.scene.control.Label messageLabel = new javafx.scene.control.Label(message);
    messageLabel.getStyleClass().add("custom-alert-label");

    Button okButton = new Button("OK");
    okButton.getStyleClass().add("custom-alert-button");
    okButton.setOnAction(e -> alertStage.close());

    alertBox.getChildren().addAll(messageLabel, okButton);

    Scene scene = new Scene(alertBox, 300, 150);

    scene.getStylesheets().add(getClass().getResource("/tictactoe/styles.css").toExternalForm());

    alertStage.setScene(scene);
    alertStage.showAndWait();
}

    public FXMLLoader navigateToScreen(ActionEvent event, String fxml, String title) {
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not navigate to " + title + " screen.");
        }
        return loader;
    }
    
    @FXML
    private void handleBackButton(ActionEvent event) {
        navigateToScreen(event, "Menu.fxml", "Menu");
    }
}