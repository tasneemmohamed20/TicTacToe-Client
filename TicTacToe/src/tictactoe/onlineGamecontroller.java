package tictactoe;

import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import models.GameModel;
import models.RequsetModel;
import models.ResponsModel;

/**
 * FXML Controller class
 *
 * @author HP
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
    private Button cell1;
    @FXML
    private Button cell2;
    @FXML
    private Button cell3;
    @FXML
    private Button cell4;
    @FXML
    private Button cell5;
    @FXML
    private Button cell6;
    @FXML
    private Button cell7;
    @FXML
    private Button cell8;
    @FXML
    private Button cell9;
    @FXML
    private Button recordGame;

    private Gson gson = new Gson();
    private DataOutputStream dos;
    private DataInputStream dis;
    private String currentPlayer;
    private boolean isPlayerTurn = false;
    private String playerSymbol;
    private String opponentSymbol;
    private String gameId;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            PlayerSocket playerSocket = PlayerSocket.getInstance();
            dos = playerSocket.getDataOutputStream();
            dis = playerSocket.getDataInputStream();

            fetchGameData();
            startServerListener();
        } catch (IOException ex) {
            showError("Connection Error", "Failed to connect to the server.");
        }
    }

   private void fetchGameData() {
    try {
        RequsetModel request = new RequsetModel("initGame", null);
        dos.writeUTF(gson.toJson(request));
        dos.flush();

        String response = dis.readUTF();
        ResponsModel responsModel = gson.fromJson(response, ResponsModel.class);

        if (("gameStart".equals(responsModel.getStatus()) && responsModel.getData() != null) || ("success".equals(responsModel.getStatus()) && responsModel.getData() != null) ) {
            GameModel gameData = gson.fromJson(gson.toJson(responsModel.getData()), GameModel.class);
            startGame(gameData);
        } else if ("error".equals(responsModel.getStatus())) {
            throw new IllegalStateException(responsModel.getMessage());
        } else {
            System.out.println("Unexpected status: " + responsModel.getStatus());
            throw new IllegalStateException("Unexpected response status: " + responsModel.getStatus());
        }
    } catch (Exception ex) {
        showError("Game Initialization Failed", "Failed to fetch game data: " + ex.getMessage());
        ex.printStackTrace();
    }
}


    public void startGame(GameModel game) {
        if (game == null) {
            showError("Error", "Game data is null. Cannot start the game.");
            return;
        }

        this.currentPlayer = game.getCurrentPlayer();
        this.playerSymbol = game.isPlayerTurn() ? game.getPlayer1Symbol() : game.getPlayer2Symbol();
        this.opponentSymbol = playerSymbol.equals("X") ? "O" : "X";
        this.gameId = game.getGameId();

        Platform.runLater(() -> {
            labelPlayerX.setText(game.getPlayer1());
            labelPlayerO.setText(game.getPlayer2());
            labelScoreX.setText("0");
            labelScoreO.setText("0");
            resetBoard();
        });

        isPlayerTurn = game.isPlayerTurn();
        System.out.println("Game started successfully. Game ID: " + gameId);
    }

    private void startServerListener() {
        Thread listenerThread = new Thread(() -> {
            while (true) {
                try {
                    String response = dis.readUTF();
                    ResponsModel serverResponse = gson.fromJson(response, ResponsModel.class);

                    Platform.runLater(() -> handleServerResponse(serverResponse));
                } catch (IOException ex) {
                    Platform.runLater(() -> showError("Connection Error", "Disconnected from the server."));
                    break;
                }
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    private void handleServerResponse(ResponsModel response) {
        if (response == null) {
            System.out.println("Unknown response received.");
            return;
        }

        switch (response.getStatus()) {
            case "move":
                updateBoard((Map<String, String>) response.getData());
                break;
            case "gameOver":
                handleGameOver(response.getMessage());
                break;
            default:
                System.out.println("Unknown response: " + response.getStatus());
                break;
        }
    }

    private void updateBoard(Map<String, String> moveData) {
        if (moveData == null || !moveData.containsKey("cell") || !moveData.containsKey("symbol")) {
            System.out.println("Invalid move data received.");
            return;
        }

        String cellId = moveData.get("cell");
        String symbol = moveData.get("symbol");

        Button targetCell = getCellById(cellId);
        if (targetCell != null) {
            targetCell.setText(symbol);
            targetCell.setDisable(true);
        }

        isPlayerTurn = !symbol.equals(playerSymbol);
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

    private void handleGameOver(String resultMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(resultMessage);
        alert.showAndWait();
        resetBoard();
    }

    private void resetBoard() {
        Platform.runLater(() -> {
            cell1.setText("");
            cell1.setDisable(false);
            cell2.setText("");
            cell2.setDisable(false);
            cell3.setText("");
            cell3.setDisable(false);
            cell4.setText("");
            cell4.setDisable(false);
            cell5.setText("");
            cell5.setDisable(false);
            cell6.setText("");
            cell6.setDisable(false);
            cell7.setText("");
            cell7.setDisable(false);
            cell8.setText("");
            cell8.setDisable(false);
            cell9.setText("");
            cell9.setDisable(false);
        });
    }

    @FXML
    private void handleCellAction(ActionEvent event) {
        if (!isPlayerTurn) {
            showError("Invalid Move", "It's not your turn!");
            return;
        }

        Button clickedButton = (Button) event.getSource();
        if (!clickedButton.getText().isEmpty()) {
            showError("Invalid Move", "Cell is already occupied!");
            return;
        }

        String cellId = clickedButton.getId();
        clickedButton.setText(playerSymbol);
        clickedButton.setDisable(true);

        sendMove(cellId);
    }

    private void sendMove(String cellId) {
        try {
            Map<String, String> moveData = new HashMap<>();
            moveData.put("gameId", gameId);
            moveData.put("cell", cellId);
            moveData.put("symbol", playerSymbol);

            RequsetModel moveRequest = new RequsetModel("move", moveData);
            dos.writeUTF(gson.toJson(moveRequest));
            dos.flush();

            isPlayerTurn = false;
        } catch (IOException ex) {
            showError("Error", "Failed to send move to the server.");
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}