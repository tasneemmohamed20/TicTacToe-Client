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
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private FXMLLoader navigateToScreen(ActionEvent event, String fxml, String title) {
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
}
