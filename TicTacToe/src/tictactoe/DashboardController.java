package tictactoe;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
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
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.GameModel;
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
    Gson gson = new Gson();
    private String invitedUser;
    private String userScore;
    private String userName;

    String currentName;
    DataOutputStream dos;
    DataInputStream dis;

    public void setScore(String playerscore) {
        userScore = playerscore;
        if (userScore != null) {
            score.setText(userScore);
        } else {
            System.out.println("Error: userScore is null!");
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
            dis = playerSocket.getDataInputStream();
            fetchOnlineUsers(dos, dis);
            refresh(dos, dis);

            Platform.runLater(() -> {
                if (score != null && score.getScene() != null) {
                    score.getScene().getWindow().setOnCloseRequest(event -> cleanupResources());
                } else {
                    System.err.println("Score or its scene is not initialized.");
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void fetchOnlineUsers(DataOutputStream dos, DataInputStream dis) {
        try {
            String jsonRequest = gson.toJson(new RequsetModel("fetchOnline", null));
            dos.writeUTF(jsonRequest);
            dos.flush();

            String responseOnlineUsers = dis.readUTF();
            ResponsModel res = gson.fromJson(responseOnlineUsers, ResponsModel.class);
            System.out.println("Status: " + res.getStatus());
            System.out.println("Data: " + res.getData());
            if ("success".equals(res.getStatus())) {
                List<String> users = (List<String>) res.getData();
                currentName = users.get(users.size() - 1);
                score.setText(userScore);
                displayOnlineUsers(users);
            }
        } catch (IOException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    volatile Thread t;
    private volatile boolean running = true;

    private void refresh(DataOutputStream dos, DataInputStream dis) {
        t = new Thread(() -> {
            boolean isRefOn = true;
            while (running) {
                try {
                    if (!running || Thread.interrupted()) {
                        System.out.println("Refresh Thread stopped");
                        break;
                    };

                    /* if (isRefOn) {
                        isRefOn = false;
                        String jsonRequest = gson.toJson(new RequsetModel("fetchOnline", null));

                        dos.writeUTF(jsonRequest);
                        dos.flush();
                    }*/
                    String jsonRequest = gson.toJson(new RequsetModel("fetchOnline", null));
                    dos.writeUTF(jsonRequest);
                    dos.flush();

                    String responseOnlineUsers = dis.readUTF();
                    ResponsModel res = gson.fromJson(responseOnlineUsers, ResponsModel.class);

                    if (!running) {
                        break;
                    }

                    System.err.println("!!!!!!!!!!! response of refresh" + res.toString());
                    switch (res.getStatus()) {
                        case "success":
                            List<String> users = (List<String>) res.getData();

                            Platform.runLater(() -> {
                                displayOnlineUsers(users);
                                currentName = users.get(users.size() - 1);
                            });
                            break;

                        case "invitation":
                            System.out.println("Invitation received: " + res.getMessage());
                            System.out.println("Invitation received: " + res.getData());
                            Platform.runLater(() -> showInviteAlert(res.getMessage(), res.getData()));
                            break;

                        case "wait":
                            System.out.println("Wait received: " + res.getMessage());
                            Platform.runLater(() -> showAlert(res.getMessage()));
                            break;

                        case "cancel":
                            System.out.println(res.getMessage());
                            Platform.runLater(() -> showAlert(res.getMessage()));
                            break;
                        case "gameStart":
                            running = false;
                            if (t != null && t.isAlive()) {
                                t.interrupt();
                            }
                            stopRefreshThread();
                            System.out.println("Game start received: " + res.getData());
                            GameModel gameData = gson.fromJson(gson.toJson(res.getData()), GameModel.class);
                            Platform.runLater(() -> {
                                startGame(gameData);
                                System.out.println("Refresh thread status: " + (t != null && t.isAlive()));
                            });
                            break;
                        case "info":
                            System.out.println("Info message: " + res.getMessage());
                            break;
                        case "notAllowed":
                            Platform.runLater(() -> showAlert("You are not allowed to play. Please wait or check eligibility."));
                            break;
                        default:
                            System.out.println("Unknown status in dashboard: " + res.getStatus());
                            break;
                    }
                    Thread.sleep(5000);
                } catch (IOException ex) {
                    if (!running) {
                        break;
                    }
                    Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    System.out.println("Thread stopped");
                    break;
                    // } finally {
                    //     System.out.println("Refresh thread terminated");
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void displayOnlineUsers(List<String> users) {
    Platform.runLater(() -> {
        try {
            if (users == null) {
                System.err.println("Users list is null.");
                return;
            }

            if (userName == null) {
                System.err.println("Username is null.");
                return;
            }

            List<String> filteredUsers = users.stream()
                    .filter(user -> user != null && !user.equals(userName))
                    .collect(Collectors.toList());

            ObservableList<String> observableList = FXCollections.observableArrayList(filteredUsers);
            final boolean[] isAnyButtonDisabled = {false};

            onlineusers.setCellFactory(lv -> new ListCell<String>() {
                private final HBox content;
                private final Label label;
                private final Button inviteButton;

                {
                    label = new Label();
                    inviteButton = new Button("Invite");

                    inviteButton.setOnAction(event -> {
                        invitedUser = getItem();
                        sendInvite(currentName, invitedUser);
                        isAnyButtonDisabled[0] = true;
                        inviteButton.setText("Invited");
                        updateAllCells();
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
                        inviteButton.setDisable(isAnyButtonDisabled[0]); // Disable button if invite sent
                        setGraphic(content);
                    }
                }
            });
            onlineusers.setItems(observableList);
        } catch (Exception ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, "Error in displayOnlineUsers", ex);
            ex.printStackTrace();
        }
    });
}
 void updateAllCells() {
                onlineusers.refresh();
            }

    void sendInvite(String sender, String receiver) {
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

    void cancelInvite(Object data) {
        try {
            RequsetModel requsetModel = new RequsetModel("cancel", data);

            String inviteJson = gson.toJson(requsetModel);
            dos.writeUTF(inviteJson);

        } catch (IOException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean validateGameModel(GameModel game) {
        System.out.println("!!!!!!" + game.toString());
        return game != null
                && game.getGameId() != null
                && game.getPlayer1() != null
                && game.getPlayer1Symbol() != null
                && game.getPlayer2() != null
                && game.getPlayer2Symbol() != null;

    }

    private void acceptInvite(Object data) {
        try {
            String jsonRequest = gson.toJson(new RequsetModel("accept", data));
            dos.writeUTF(jsonRequest);
            dos.flush();

            System.out.println("Accepted invite, waiting for game to start...");
        } catch (IOException ex) {
            System.err.println("Error accepting invite: " + ex.getMessage());
            showAlert("Failed to accept the invite. Please check your connection.");
        }
    }

    private void startGame(GameModel game) {
        try {
            stopRefreshThread();
            if (!validateGameModel(game)) {
                Platform.runLater(() -> showError("Error", "Invalid game data received."));
                return;
            }
            Platform.runLater(() -> navigateToGame(game));
        } catch (Exception e) {
            Platform.runLater(() -> showError("Game Error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    private synchronized void stopRefreshThread() {
        running = false;

        if (t != null) {
            try {
                t.interrupt();
                t.join(2000);

                if (t.isAlive()) {
                    System.err.println("Thread did not stop within timeout");
                    t = null;
                }
//                if (dis != null) dis.close();
//                if (dos != null) dos.close();
            } catch (InterruptedException ex) {
                System.err.println("Thread interruption error: " + ex.getMessage());
//            } catch (IOException ex) {
//                Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        System.out.println("Refresh thread stopped successfully");
    }

    void showInviteAlert(String txt, Object data) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, txt, ButtonType.YES, ButtonType.NO);
        Stage currentStage = (Stage) score.getScene().getWindow();
        alert.initOwner(currentStage);
        alert.showAndWait().ifPresent(click -> {
            if (click == ButtonType.NO) {
                cancelInvite(data);
            }
            if (click == ButtonType.YES) {
                System.out.println("Accepting invitation with data: " + data);
                acceptInvite(data);
            }
        });
    }

    private void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void showAlert(String txt) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            if (score != null && score.getScene() != null) {
                Stage currentStage = (Stage) score.getScene().getWindow();
                alert.initOwner(currentStage);
            }
            alert.setHeaderText(txt);
            alert.getButtonTypes().clear();
            alert.getButtonTypes().add(ButtonType.CLOSE);
            alert.showAndWait();
        });
    }

    private void navigateToGame(GameModel game) {
        System.out.println("SHAKL ELGAMEE F ELNAVIGATE" + game.toString());
        try {
            if (!validateGameModel(game)) {
                showAlert("Invalid game data. Cannot start the game.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tictactoe/onlineGame.fxml"));
            Parent root = loader.load();

            onlineGamecontroller controller = loader.getController();
            if (controller == null) {
                showAlert("Controller is null!!");
                return;
            }

            Stage stage = (Stage) onlineusers.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            controller.setName(userName);
            controller.setScore(score.getText());
             System.out.println("From dash to game the score is " + score.getText());
            controller.initializeGameUI(game, currentName, stage);

        } catch (IOException e) {
            System.out.println(game.toString());
            System.out.println(e.toString());
            e.printStackTrace();
            showAlert("Failed to load the game screen. Please try again.");
        }
    }

    private void cleanupResources() {
        running = false;
        if (t != null && t.isAlive()) {
            t.interrupt();
        }
        try {
            if (dos != null) {
                Map<String, String> data = new HashMap<>();
                data.put("username", userName);
                String jsonRequest = gson.toJson(new RequsetModel("logout", data));
                dos.writeUTF(jsonRequest);
                dos.flush();
                System.out.println("Logout request sent to server.");
            }
        } catch (IOException e) {
            System.err.println("Error sending logout request: " + e.getMessage());
        }
        try {
            if (dos != null) {
                dos.close();
            }
            if (dis != null) {
                dis.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
        System.out.println("Resources cleaned up, thread stopped.");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            if (dos != null) {
                Map<String, String> data = new HashMap<>();
                data.put("username", userName);
                String jsonRequest = gson.toJson(new RequsetModel("logout", data));
                dos.writeUTF(jsonRequest);
                dos.flush();
                System.out.println("Logout request sent to server.");
            }
        } catch (IOException e) {
            System.err.println("Error sending logout request: " + e.getMessage());
        }
        cleanupResources();
        navigateToLoginScreen();
    }

    private void navigateToLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tictactoe/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) logout.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading login screen: " + e.getMessage());
            showAlert("Failed to navigate to the login screen. Please try again.");
        }
    }

    @FXML
    private void navToRecods(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AllRecords.fxml"));
            Parent root = loader.load();
            AllRecordsController controller = loader.getController();
            controller.setName(userName);
            controller.setScore(score.getText());
            controller.setIsOnline(true);
            Stage stage = (Stage) onlineusers.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
