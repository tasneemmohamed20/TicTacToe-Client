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

/**
 * FXML Controller class
 */
public class LoginController implements Initializable {

    @FXML
    private TextField usernameTF;
    @FXML
    private PasswordField passTF;
    @FXML
    private Button loginBTN;
    @FXML
    private Button signupNav;

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
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not navigate to the signup page.");
        }
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

            Socket socket = new Socket("localhost", 5005);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            UserModel user = new UserModel(0, username, password, "0", "online");
            RequsetModel request = new RequsetModel("login", user);

            String jsonRequest = new Gson().toJson(request);
            dos.writeUTF(jsonRequest);
            dos.flush();

            String jsonResponse = dis.readUTF();
            ResponsModel response = new Gson().fromJson(jsonResponse, ResponsModel.class);

            if ("success".equals(response.getStatus())) {
                showAlert(Alert.AlertType.INFORMATION, "Login Successful", response.getMessage());
                
                 try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LocalHvsH.fxml"));
            Parent signupRoot = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene signupScene = new Scene(signupRoot);
            stage.setScene(signupScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not navigate to the signup page.");
        }
                
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", response.getMessage());
            }

            dis.close();
            dos.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Connection Error", "Failed to connect to the server: " + e.getMessage());
        }
    }

    

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
