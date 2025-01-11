package tictactoe;

import DAO.DAO;
import Users.Users;
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

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignupController {

    @FXML
    private Button signupBTN;
    @FXML
    private Button loginNav;
    @FXML
    private TextField usernameTF;
    @FXML
    private TextField passTF;

    /**
     * Handles the signup process by validating input and adding the user to the database.
     *
     * @param event Mouse event triggered by clicking the signup button.
     */
    @FXML
    public void handleSignup(MouseEvent event) {
        String username = usernameTF.getText().trim();
        String password = passTF.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Username and password cannot be empty.");
            return;
        }

        try {
            DAO dao = new DAO();
            Users user = new Users(username, password);
            int result = dao.addUser(user);

            if (result > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Signup successful!");
                clearFields();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();

         
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            } else {
                showAlert(Alert.AlertType.ERROR, "Signup Failed", "Could not sign up. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error: " + e.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(SignupController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Clears the input fields.
     */
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
           
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();

         
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not navigate to the login page. Please check if Login.fxml exists.");
        }
    }
}
