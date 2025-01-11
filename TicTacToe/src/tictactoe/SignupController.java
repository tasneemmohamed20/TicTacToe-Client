package tictactoe;

import DAO.DAO;
import Users.Users;
import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import models.RequsetModel;
import models.ResponsModel;
import models.UserModel;



import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class SignupController {

    @FXML
    private Button signupBTN;
    @FXML
    private Button loginNav;
    @FXML
    private TextField usernameTF;
    @FXML
    private TextField passTF;

    private PlayerSocket playerSocket;


    @FXML

    public void handleSignup(ActionEvent event) {
        try {
            String username = usernameTF.getText().trim();
            String password = passTF.getText().trim();
            
            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Username and password cannot be empty");
                return;
            }
    
            Socket socket = new Socket("localhost", 5005); // Create new socket connection
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            
            UserModel user = new UserModel(0, username, password, "0", "offline");
            RequsetModel request = new RequsetModel("register", user);
            
            String jsonRequest = new Gson().toJson(request);
            dos.writeUTF(jsonRequest); // Use writeUTF instead of println
            dos.flush(); // Make sure to flush the stream
            
            String jsonResponse = dis.readUTF(); // Use readUTF instead of readLine
            ResponsModel response = new Gson().fromJson(jsonResponse, ResponsModel.class);
    
            if (response.getStatus().equals("success")) {
                showAlert(Alert.AlertType.INFORMATION, "Success", response.getMessage());
                clearFields();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            	Parent root = loader.load();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", response.getMessage());
            }
            
            // Close resources
            dis.close();
            dos.close();
            socket.close();
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Connection failed: " + e.getMessage());
        }
    }



    private void clearFields() {
        usernameTF.clear();
        passTF.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleLoginNavigation(ActionEvent event) {
        try {
            // Load the Login.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();

            // Get the current stage and set the new scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not navigate to the login page. Please check if Login.fxml exists.");
        }
    }
}