package tictactoe;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import models.GameModel;
import models.GameRecord;
import models.Move;
import models.RequsetModel;
import models.ResponsModel;
import theGame.XO;

/**
 * FXML Controller class to manage the online Tic Tac Toe game.
 */
public class onlineGamecontroller implements Initializable {

    @FXML
    private Label labelPlayerX;
    @FXML
    private Label labelScoreX;
    @FXML
    private Label labelPlayerO;
    @FXML
    private Label labelScoreO;
    @FXML
    private Button cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8, cell9;
    @FXML
    private Button recordGame;

    private final Gson gson = new Gson();
    private DataOutputStream dos;
    private DataInputStream dis;
    private String currentPlayer;
    private boolean isPlayerTurn = false;
    private String playerSymbol;
    private String opponentSymbol;
    private String gameId;

    private GameModel gameData;
    private GameModel gameModel;
    GameRecord record;
    boolean isRecording = false;
    private final Image xImage = new Image("/assets/x.png");
    private final Image oImage = new Image("/assets/o.png");

    private int scoreX = 0;
    private int scoreO = 0;
    String userName;
    private Thread t;
    private boolean running = true;

    public void setName(String name) {
        userName = name;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            PlayerSocket playerSocket = PlayerSocket.getInstance();
            dos = playerSocket.getDataOutputStream();
            dis = playerSocket.getDataInputStream();

            if (gameData != null) {
                startGame(gameData);
            }

            startServerListener();
        } catch (IOException ex) {
            showError("Connection Error", "Failed to connect to the server.");
        }
    }

    public void initializeGame(GameModel game) {
        if (game == null) {
            throw new IllegalArgumentException("Game data cannot be null");
        }
        this.gameData = game;
    }

    public void startGame(GameModel game) {
        if (game == null) {
            showError("Error", "Game data is null. Cannot start the game.");
            return;
        }
        if (game.getPlayer1() == null || game.getPlayer2() == null) {
            showError("Error", "Player names are missing.");
            return;
        }

        this.playerSymbol = game.getCurrentPlayer().equals(game.getPlayer1())
                ? game.getPlayer1Symbol() : game.getPlayer2Symbol();
        this.opponentSymbol = game.getCurrentPlayer().equals(game.getPlayer1())
                ? game.getPlayer2Symbol() : game.getPlayer1Symbol();
        this.gameId = game.getGameId();

        Platform.runLater(() -> {
            // Update player labels with symbol and turn indication
            labelPlayerX.setText(game.getPlayer1() + " (X)");

            labelPlayerO.setText(game.getPlayer2() + " (O)");

            labelScoreX.setText("0");
            labelScoreO.setText("0");
            resetBoard();
        });

        isPlayerTurn = game.getCurrentPlayer().equals(currentPlayer);
        System.out.println(currentPlayer);
        System.out.println("Game started successfully. Game ID: " + gameId);
    }

    void initializeGameUI(GameModel game, String currentPlayer, Stage stage) {
        this.currentPlayer = currentPlayer;
        System.out.println("initializeGameUI currentPlayer" + currentPlayer);
        labelPlayerX.setText(game.getPlayer1() + " (" + game.getPlayer1Symbol() + ")");
        labelPlayerO.setText(game.getPlayer2() + " (" + game.getPlayer2Symbol() + ")");
        labelScoreX.setText("0");
        labelScoreO.setText("0");
        this.gameData = game;
        resetBoard();

        String[] board = game.getBoard();
        for (int i = 0; i < board.length; i++) {
            String cellId = "cell" + (i + 1);
            Button cell = getCellById(cellId);
            if (cell != null) {
                cell.setText(board[i] == null ? "" : board[i]);
                cell.setDisable(board[i] != null);
            }
        }

        /* stage.setOnCloseRequest((event) -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to Withdraw?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(click -> {
                if (click == ButtonType.YES) {
                    sendWithdrawRequest();

                    Platform.exit();
                } else if (click == ButtonType.NO) {
                    event.consume();
                }

            });
        });*/
    }

    private void startServerListener() {
        t = new Thread(() -> {
            while (running) {
                try {
                    String responseString = dis.readUTF();
                    System.out.println("[DEBUG] Received message from server: " + responseString);
                    ResponsModel response = gson.fromJson(responseString, ResponsModel.class);
                    Platform.runLater(() -> handleServerResponse(response));
                } catch (IOException | JsonSyntaxException ex) {
                    Platform.runLater(() -> showErrorOnServerClose("Connection Error",
                            "Disconnected from the server: " + ex.getMessage()));
                    break;
                }
            }
        });
        t.start();
    }

    private void handleServerResponse(ResponsModel response) {
        if (response == null) {
            System.out.println("Received an empty or null response.");
            return;
        }

        if (response.getData() != null) {

            gameModel = gson.fromJson(gson.toJson(response.getData()), GameModel.class);
            System.out.println("gameModel : " + gameModel.getCurrentPlayer());
        }

        switch (response.getStatus()) {
            case "gameStart":
                if (response.getData() != null) {
                    GameModel gameData = gson.fromJson(gson.toJson(response.getData()), GameModel.class);
                    startGame(gameData);
                } else {
                    showError("Data Error", "Game data is missing.");
                }
                break;

            case "makeMove":
                if (response.getData() != null) {
                    updateBoard((Map<String, String>) response.getData());
                } else {
                    showError("Data Error", "Move data is missing.");
                }
                break;

            case "gameOver":
                System.out.println("======================================");
                System.out.println(response.getMessage());
                System.out.println(response.getData());
                System.out.println("======================================");
                updateScores((Map<String, String>) response.getData(), response.getMessage());
                showVideoAlert(response.getMessage(), response.getMessage());

                //handleGameOver(response.getMessage(), winner);
                break;

            case "info":
                System.out.println("Info message: " + response.getMessage());
                Platform.runLater(() -> {
                    if (response.getMessage().contains("Your turn")) {
                        isPlayerTurn = true;

                        // Update labels to show current turn
                        labelPlayerX.setText(gameData.getPlayer1() + " (X)");

                        labelPlayerO.setText(gameData.getPlayer2() + " (O)");
                    }
                });
                break;

            case "error":
                showError("Server Error", response.getMessage());
                break;
            case "errorFromServer":
                showErrorOnServerClose("Server Error", response.getMessage());
                break;
            case "update":
                if (response.getData() != null) {
                    try {
                        Map<String, Object> updateData = gson.fromJson(
                                gson.toJson(response.getData()),
                                new TypeToken<Map<String, Object>>() {
                                }.getType()
                        );

                        String[] boardState = gson.fromJson(
                                gson.toJson(updateData.get("board")),
                                String[].class
                        );
                        String currentTurn = (String) updateData.get("currentTurn");

                        Platform.runLater(() -> {
                            for (int i = 0; i < boardState.length; i++) {
                                if (boardState[i] != null) {
                                    String cellId = "cell" + (i + 1);
                                    Button cell = getCellById(cellId);
                                    if (cell != null) {
                                        if (boardState[i].equals("X")) {
                                            setCellImage(cell, xImage);
                                        } else {
                                            setCellImage(cell, oImage);
                                        }
                                        cell.setDisable(true);
                                    }
                                    if (isRecording) {
                                        record.saveMove(new Move(boardState[i], cellId));

                                    }
                                }
                            }

                            // Update labels to show current turn
                            /* labelPlayerX.setText(gameData.getPlayer1() + " (X)"
                                    + (playerSymbol.equals("X") ? " [You]" : "")
                                    + (currentTurn.equals("X") ? " (Your Turn)" : ""));

                            labelPlayerO.setText(gameData.getPlayer2() + " (O)"
                                    + (playerSymbol.equals("O") ? " [You]" : "")
                                    + (currentTurn.equals("O") ? " (Your Turn)" : ""));

                            isPlayerTurn = currentTurn.equals(playerSymbol);
                            System.out.println("[DEBUG] Board updated. Current turn: " + currentTurn
                                    + ", Player symbol: " + playerSymbol
                                    + ", isPlayerTurn: " + isPlayerTurn);*/
                        });
                    } catch (Exception e) {
                        System.err.println("Error parsing update data: " + e.getMessage());
                    }
                }
                break;
            case "withdraw":
                Platform.runLater(() -> {
                    //showError(currentPlayer, currentPlayer + " withdrawed");
                    showVideoAlert(response.getMessage(), "withdraw");
                    navigateToMenu();
                });
                break;

            default:
                System.out.println("Unknown status in controller: " + response.getStatus());
        }
    }

    private Button getCellById(String cellId) {
        switch (cellId) {
            case "cell1":
                return cell1;
            case "cell2":
                return cell2;
            case "cell3":
                return cell3;
            case "cell4":
                return cell4;
            case "cell5":
                return cell5;
            case "cell6":
                return cell6;
            case "cell7":
                return cell7;
            case "cell8":
                return cell8;
            case "cell9":
                return cell9;
            default:
                return null;
        }
    }

    private void handleGameOver(String resultMessage, String winner) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(resultMessage);
        alert.showAndWait();

        if (winner != null) {
            String videoPath = winner.equals(playerSymbol) ? "/assets/bravo.mp4" : "/assets/looser.mp4";
            showVideoAlert(resultMessage, "/assets/bravo.mp4");
        }

        resetBoard();
    }

    private String determineWinner(String resultMessage) {
        if (resultMessage.contains(playerSymbol)) {
            return playerSymbol;
        } else if (resultMessage.contains(opponentSymbol)) {
            return opponentSymbol;
        } else {
            return null;
        }
    }

    private void resetBoard() {
        Platform.runLater(() -> {
            for (int i = 1; i <= 9; i++) {
                Button cell = getCellById("cell" + i);
                if (cell != null) {
                    cell.setText("");
                    cell.setDisable(false);
                }
            }
        });
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    private void showErrorOnServerClose(String title, String message) {
        Platform.runLater(() -> {
            try {
                if (labelPlayerX == null || labelPlayerX.getScene() == null) {
                    System.err.println("UI elements are not initialized.");
                    return;
                }

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tictactoe/dashboard.fxml"));
                        Parent root = loader.load();

                        DashboardController dashboardController = loader.getController();
                        dashboardController.setName(userName);
                        dashboardController.setScore(String.valueOf(scoreX));

                        Stage currentStage = (Stage) labelPlayerX.getScene().getWindow();
                        currentStage.close();

                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Dashboard");
                        stage.show();
                    } catch (IOException ex) {
                        Logger.getLogger(onlineGamecontroller.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(onlineGamecontroller.class.getName()).log(Level.SEVERE, "Error in showErrorOnServerClose", ex);
                ex.printStackTrace();
            }
        });
    }

    @FXML
    private void handleCellAction(ActionEvent event) {
        Button clickedCell = (Button) event.getSource();
        String cellId = clickedCell.getId();

        if (!isPlayerTurn) {
            showError("Invalid Move", "It's not your turn!");
            return;
        }

        if (!clickedCell.getText().isEmpty()) {
            showError("Invalid Move", "This cell is already occupied!");
            return;
        }

        try {

            Map<String, String> moveData = new HashMap<>();
            moveData.put("cell", cellId);
            moveData.put("symbol", playerSymbol);

            if (playerSymbol.equals("X")) {
                setCellImage(clickedCell, xImage);
            } else {
                setCellImage(clickedCell, oImage);
            }

            RequsetModel request = new RequsetModel("makeMove", moveData);
            dos.writeUTF(gson.toJson(request));
            dos.flush();

            isPlayerTurn = false;
        } catch (IOException ex) {
            showError("Connection Error", "Failed to send move to server: " + ex.getMessage());
        }
    }

    private void updateBoard(Map<String, String> moveData) {
        if (moveData == null || !moveData.containsKey("cell") || !moveData.containsKey("symbol")) {
            return;
        }

        String cellId = moveData.get("cell");
        String symbol = moveData.get("symbol");

        Button targetCell = getCellById(cellId);
        if (targetCell != null) {
            if (symbol.equals("X")) {
                setCellImage(targetCell, xImage);
            } else {
                setCellImage(targetCell, oImage);
            }
            targetCell.setDisable(true);
        }
    }

    private void setCellImage(Button cell, Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);
        cell.setGraphic(imageView);
    }

    private void showVideoAlert(String title, String winner) {
        try {
            String videoPath;

            if (winner.equals(currentPlayer + " wins!")) {
                videoPath = "/assets/bravo.mp4";
            } else if (winner.equals("withdraw")) {
                videoPath = "/assets/bravo.mp4";
            } else if (winner.equals("It's a draw!")) {
                videoPath = "/assets/draw.mp4";
            } else {
                videoPath = "/assets/looser.mp4";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tictactoe/VideoLayout.fxml"));

            Parent root = loader.load();

            VideoLayoutController controller = loader.getController();

            controller.initialize(videoPath);
            controller.setWinnerText(title);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            controller.setOnNewGameAction(() -> {
                System.out.println("Starting a new game...");
                resetBoard();
                stage.close();
            });

        } catch (Exception e) {
            e.printStackTrace();
            //showAlert("Error", "Could not load or play video!", Alert.AlertType.ERROR);
        }
    }

    private void updateScores(Map<String, String> data, String winner) {
        String player1 = data.get("player1");
        String player2 = data.get("player2");

        String str = winner;
        String[] parts = str.split(" ");
        String winnerName = parts[0];
        sendScoreToServer(winnerName);

        if (winner.equals(player1 + " wins!")) {
            scoreX += 10;
            labelScoreX.setText(String.valueOf(scoreX));
        } else if (winner.equals(player2 + " wins!")) {
            scoreO += 10;
            labelScoreO.setText(String.valueOf(scoreO));
        }
    }

    private void sendScoreToServer(String name) {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("name", name);
            PlayerSocket playerSocket = PlayerSocket.getInstance();
            DataOutputStream dos = playerSocket.getDataOutputStream();
            String jsonRequest = new Gson().toJson(new RequsetModel("updateScore", data));
            dos.writeUTF(jsonRequest);
            System.out.println("tictactoe.LocalHvsHcontroller.sendScoreToServer()");
        } catch (IOException ex) {
            Logger.getLogger(LocalHvsHcontroller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleWithdarwAction(ActionEvent event) {

        if (gameModel == null) {
            showError("Game Error", "Game data is not initialized.");
            return;
        }

        String currentTurnPlayer = gameModel.getCurrentPlayer();
        System.err.println("currentTurnPlayer : " + currentTurnPlayer);

        if (currentTurnPlayer == null || currentPlayer == null) {
            showError("Turn Error", "Unable to determine current turn.");
            return;
        }

        if (!currentPlayer.equals(currentTurnPlayer)) {
            showError("Wait Your Turn", "You can only withdraw during your turn.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Withdraw Confirmation");
        alert.setHeaderText("Are you sure you want to withdraw?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            sendWithdrawRequest();
        }
    }

    private void sendWithdrawRequest() {
        try {

            Map<String, String> data = new HashMap<>();
            data.put("player", currentPlayer);
            data.put("gameId", gameId);

            RequsetModel request = new RequsetModel("withdraw", data);
            dos.writeUTF(gson.toJson(request));
            dos.flush();

            //cleanupResources();
            Platform.runLater(() -> navigateToMenu());
        } catch (IOException ex) {
            showError("Connection Error", "Failed to send quit request: " + ex.getMessage());
        }
    }

    private void navigateToMenu() {
        // Stop any running threads
        stopRefreshThread();

        Platform.runLater(() -> {
            try {
                // Close current socket connections
                cleanupResources();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/tictactoe/dashboard.fxml"));
                Parent root = loader.load();

                DashboardController dashboardController = loader.getController();
                dashboardController.setName(userName);
                dashboardController.setScore(String.valueOf(scoreX));

                Stage currentStage = (Stage) labelPlayerX.getScene().getWindow();

                // Ensure scene creation on JavaFX thread
                Scene scene = new Scene(root);
                currentStage.setScene(scene);
                currentStage.centerOnScreen();
                currentStage.show();

            } catch (Exception ex) {
                ex.printStackTrace();
                showError("Navigation Error", "Failed to navigate to dashboard: " + ex.getMessage());
            }
        });
    }

    private void cleanupResources() {
        try {
            // Close socket connections
            if (dos != null) {
                dos.close();
            }
            if (dis != null) {
                dis.close();
            }

            // Stop any running threads
            stopRefreshThread();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private synchronized void stopRefreshThread() {
        running = false;
        if (t != null) {
            t.interrupt();
            try {
                t.join(2000); // Wait for thread to terminate
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void handleRecordButton(ActionEvent event) {
        if (!isRecording) {
            //  record.saveRecordName("rec.txt");

            isRecording = true;
            record = new GameRecord(userName + ".txt");
            record.saveRecordName();
            recordGame.setText("Stop Recording");

        } else {
            isRecording = false;
            recordGame.setText("Start Recording");
            System.out.println("Recording Stopped Game recording has been stopped.");
        }
    }

}
