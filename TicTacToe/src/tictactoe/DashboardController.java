package tictactoe;

import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import models.RequsetModel;
import models.ResponsModel;

public class DashboardController implements Initializable {

    @FXML
    private Button games;
    @FXML
    private Button logout;
    @FXML
    private Label score;
    @FXML
    private ListView<String> onlineusers;

    private Gson gson = new Gson();
    private String invitedUser;
    private boolean[] isInvited = {false};
    private String userScore;
    private String userName;
    private Timer refreshTimer;
    private DataOutputStream dos;

    public void setScore(String playerscore) {
        userScore = playerscore;
        if (userScore != null) {
            score.setText(userScore);
        }
    }

    public void setName(String playerName) {
        userName = playerName;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            PlayerSocket playerSocket = PlayerSocket.getInstance();
            dos = playerSocket.getDataOutputStream();
            DataInputStream dis = playerSocket.getDataInputStream();

            startPeriodicRefresh(dos, dis);

            Platform.runLater(() -> score.getScene().getWindow().setOnCloseRequest(event -> cleanupResources()));
        } catch (IOException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void startPeriodicRefresh(DataOutputStream dos, DataInputStream dis) {
        refreshTimer = new Timer();
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    String jsonRequest = gson.toJson(new RequsetModel("fetchOnline", null));
                    dos.writeUTF(jsonRequest);
                    dos.flush();

                    String responseOnlineUsers = dis.readUTF();
                    ResponsModel res = gson.fromJson(responseOnlineUsers, ResponsModel.class);
                    handleServerResponse(res);
                } catch (IOException ex) {
                    Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, 0, 5000);
    }

    private void handleServerResponse(ResponsModel res) {
        switch (res.getStatus()) {
            case "success":
                handleSuccessResponse(res);
                break;
            case "invitation":
                handleInvitationResponse(res);
                break;
            case "wait":
                handleWaitResponse(res);
                break;
            case "cancel":
                handleCancelResponse(res);
                break;
            default:
                handleUnknownResponse(res);
                break;
        }
    }

    private void handleSuccessResponse(ResponsModel res) {
        List<String> users = (List<String>) res.getData();
        Platform.runLater(() -> displayOnlineUsers(users));
    }

    private void handleInvitationResponse(ResponsModel res) {
        Platform.runLater(() -> showInviteAlert(res.getMessage(), res.getData()));
    }

    private void handleWaitResponse(ResponsModel res) {
        Platform.runLater(() -> showAlert(res.getMessage()));
    }

    private void handleCancelResponse(ResponsModel res) {
        Platform.runLater(() -> showAlert(res.getMessage()));
    }

    private void handleUnknownResponse(ResponsModel res) {
        System.out.println("Unknown status: " + res.getStatus());
    }

    public void displayOnlineUsers(List<String> users) {
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
                    sendInvite(userName, invitedUser);
                    inviteButton.setText("invited");
                });

                content = new HBox(20, label, inviteButton);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else if (item.equals(userName)) {
                    setGraphic(null);
                } else {
                    label.setText(item);
                    inviteButton.setDisable(isInvited[0]);
                    setGraphic(content);
                }
            }
        });
        onlineusers.setItems(observableList);
    }

    private void showInviteAlert(String message, Object data) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(click -> {
            if (click == ButtonType.NO) {
                cancelInvite(data);
            }
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.CLOSE);
        alert.showAndWait();
    }

    void cancelInvite(Object data) {
        try {

            RequsetModel requsetModel = new RequsetModel("cancel", data);

            String inviteJson = gson.toJson(requsetModel);
            dos.writeUTF(inviteJson);

        } catch (IOException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void sendInvite(String sender, String receiver) {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("sender", sender);
            data.put("receiver", receiver);

            RequsetModel requsetModel = new RequsetModel("invite", data);

            String inviteJson = gson.toJson(requsetModel);
            dos.writeUTF(inviteJson);

        } catch (IOException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void cleanupResources() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
            System.out.println("Refresh timer stopped.");
        }
        System.out.println("Window is closing.");
    }
}
