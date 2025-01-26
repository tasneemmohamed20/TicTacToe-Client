/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import models.GameRecord;

/**
 * FXML Controller class
 *
 * @author HP
 */
public class AllRecordsController implements Initializable {

    @FXML
    private ListView<String> all_records;

    /**
     * Initializes the controller class.
     */
    String displayedRecord;
    String userName;
    String score;
    boolean isOnline;

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setName(String name) {
        this.userName = name;
        if (all_records != null) {
            loadRecords();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (userName != null) {
            loadRecords();
        }
    }

    public void loadRecords() {
        GameRecord rec = new GameRecord(userName + ".txt");
        List<String> records = rec.getAllRecords();
        all_records.getItems().addAll(records);
        all_records.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                setStyle("-fx-background-color: #5A1E76; -fx-text-fill: #DCBF3F;-fx-font-weight : bold");

            }
        });

        all_records.setOnMouseClicked(e -> {
            try {
                displayedRecord = all_records.getSelectionModel().getSelectedItem();
                System.out.println(displayedRecord);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("replay_game.fxml"));
                Parent levelsRoot = loader.load();
                replay_gamecontroller replayController = loader.getController();
                replayController.setRecordName(displayedRecord);
                replayController.setUserName(userName);
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                Scene levelsScene = new Scene(levelsRoot);
                stage.setScene(levelsScene);
                stage.show();
            } catch (IOException ex) {
                Logger.getLogger(AllRecordsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    @FXML
    private void navToDash(ActionEvent event) {
        FXMLLoader loader = null;
        try {
            if (isOnline) {
                loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
                Parent root = loader.load();
                DashboardController dashboardController = loader.getController();
                dashboardController.setName(userName);
                dashboardController.setScore(score);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                loader = new FXMLLoader(getClass().getResource("Menu.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
