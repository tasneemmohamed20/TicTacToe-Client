/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe;

import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import models.RequsetModel;
import models.ResponsModel;

/**
 * FXML Controller class
 *
 * @author HP
 */
public class DashboardController implements Initializable {

    @FXML
    private Button games;
    @FXML
    private Button logout;
    @FXML
    private Label score;
    @FXML
    private ListView<String> onlineusers;
    Gson gson = new Gson();
     String invitedUser;
     boolean[] isInvited = {false};
     public void setScore(String Playerscore) {
        if (score != null) {
            score.setText(Playerscore);
        }
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            PlayerSocket playerSocket = PlayerSocket.getInstance();
            DataOutputStream dos = playerSocket.getDataOutputStream();
            DataInputStream dis = playerSocket.getDataInputStream();

            String jsonRequest = gson.toJson(new RequsetModel("fetchOnline", null));
            dos.writeUTF(jsonRequest);
            dos.flush();

            String responseOnlineUsers = dis.readUTF();
            ResponsModel res = gson.fromJson(responseOnlineUsers, ResponsModel.class);
            System.out.println("Status: " + res.getStatus());
            System.out.println("Data: " + res.getData());
            if ("success".equals(res.getStatus())) {
                List<String> users = (List<String>) res.getData();
                Platform.runLater(() -> {
                    ObservableList<String> observableList = FXCollections.observableArrayList(users);
                    onlineusers.setCellFactory(lv -> new ListCell<String>() {
                        private final HBox content;
                        private final Label label;
                        private final Button inviteButton;

                        {
                            label = new Label();
                            inviteButton = new Button("Invite");
                            
                            inviteButton.setOnAction(event -> {
                                 invitedUser = getItem();
                                 inviteButton.setText("invited");
                                 isInvited[0] = true;
                                 
                                if (invitedUser != null) {
                                    System.out.println("Invite: " + invitedUser);
                                    onlineusers.refresh();
                                }
                            });
                            
                            content = new HBox(20, label, inviteButton); 
                        }

                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) {
                                setGraphic(null);
                            } else {
                                label.setText(item);
                                inviteButton.setDisable(isInvited[0]);
                                setGraphic(content);
                            }
                        }
                    });
                    onlineusers.setItems(observableList);
                });

            }
        } catch (IOException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
