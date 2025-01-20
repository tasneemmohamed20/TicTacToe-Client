package tictactoe;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

    private final Gson gson = new Gson();
    private DataOutputStream dos;
    private DataInputStream dis;
    private String currentPlayer;
    private boolean isPlayerTurn = false;
    private String playerSymbol;
    private String opponentSymbol;
    private String gameId;
    @FXML
    private Button recordGame;

    private GameModel gameData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            PlayerSocket playerSocket = PlayerSocket.getInstance();
            dos = playerSocket.getDataOutputStream();
            dis = playerSocket.getDataInputStream();

            if (gameData != null) {
                startGame(gameData); //1
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

    // private void fetchGameData() {
    //     try {
    //         RequsetModel request = new RequsetModel("gameStart", null);
    //         System.out.println("!!!!!!!!!!!!!!"+request.toString());
    //         dos.writeUTF(gson.toJson(request));
    //         dos.flush();
    //         System.out.println("[DEBUG] Game initialization request sent.");
    //         String response = dis.readUTF();
    //         ResponsModel responsModel = gson.fromJson(response, ResponsModel.class);
    //         System.out.println("[DEBUG] Received response: " + response);
    //         System.err.println("ResponseModel"+responsModel.toString());
    //         switch (responsModel.getStatus()) {
    //             case "gameStart":
    //                 GameModel gameData = gson.fromJson(gson.toJson(responsModel.getData()), GameModel.class);
    //                 startGame(gameData);
    //                 break;
    //             case "error":
    //                 showError("Initialization Error", responsModel.getMessage());
    //                 break;
    //             default:
    //                 showError("Initialization Error", "Unexpected response: " + responsModel.getStatus());
    //         }
    //     } catch (IOException ex) {
    //         showError("Connection Error", "Failed to communicate with the server: " + ex.getMessage());
    //     }
    // }

    public void startGame(GameModel game) {
        System.out.println("!!!!!!!!!!!!!!"+game.toString()); // 9
        if (game == null) {
            showError("Error", "Game data is null.");
            return;
        }

        this.gameId = game.getGameId();
        this.currentPlayer = game.getCurrentPlayer();
        this.isPlayerTurn = game.isPlayerTurn();
        this.playerSymbol = game.getPlayer1Symbol();
        this.opponentSymbol = game.getPlayer2Symbol();

        initializeGameUI(game);
    }

    private void initializeGameUI(GameModel game) {
        labelPlayerX.setText(game.getPlayer1() + " (" + game.getPlayer1Symbol() + ")");
        labelPlayerO.setText(game.getPlayer2() + " (" + game.getPlayer2Symbol() + ")");
        labelScoreX.setText("0");
        labelScoreO.setText("0");

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
    }

    private void startServerListener() {
        Thread listenerThread = new Thread(() -> {
            try {
                while (true) {
                    String response = dis.readUTF();
                    System.out.println("[DEBUG] Received raw server response: " + response); //2 //4 //6
                    try {
                        ResponsModel serverResponse = gson.fromJson(response, ResponsModel.class);
                        System.out.println("[DEBUG] Parsed server response: " + serverResponse); //3  //5 //7
                        if (serverResponse != null) {
                            Platform.runLater(() -> handleServerResponse(serverResponse));
                        }
                    } catch (JsonSyntaxException e) {
                        System.err.println("[ERROR] Invalid JSON response: " + response);
                        System.err.println("[ERROR] Exception: " + e.getMessage());
                        Platform.runLater(() -> showError("Server Error", "Received invalid response from server"));
                        System.err.println("invalid: " + response.toString());
                    }
                }
            } catch (IOException ex) {
                System.err.println("[ERROR] Disconnected from server: " + ex.getMessage());
                Platform.runLater(() -> showError("Connection Error", "Disconnected from the server."));
            }
        });
    
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    private void handleServerResponse(ResponsModel response) {
        if (response == null) {
            System.out.println("Received an empty or null response.");
            return;
        }

        System.err.println("[DEBUG] Handling server response: " + response.getData()); // 8 with data // 10 with null data
        switch (response.getStatus()) {
            case "move":
                if (response.getData() != null) {
                    updateBoard((Map<String, String>) response.getData());
                } else {
                    showError("Data Error", "Move data is missing.");
                }
                break;
            case "gameStart":
                if (response.getData() != null) {
                    GameModel gameData = gson.fromJson(gson.toJson(response.getData()), GameModel.class);
                    startGame(gameData);
                } else {
                    showError("Data Error", "Game data is missing.");
                }
                break;
            case "gameOver":
                handleGameOver(response.getMessage());
                break;
            case "info":
                System.out.println("Info message: " + response.getMessage());
                break;
            case "error":
                showError("Server Error", response.getMessage());
                break;
            default:
                System.out.println("Unknown status: " + response.getStatus());
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
            targetCell.setText(symbol);
            targetCell.setDisable(true);
        }

        isPlayerTurn = !symbol.equals(playerSymbol);
    }

    private Button getCellById(String cellId) {
        switch (cellId) {
            case "cell1": return cell1;
            case "cell2": return cell2;
            case "cell3": return cell3;
            case "cell4": return cell4;
            case "cell5": return cell5;
            case "cell6": return cell6;
            case "cell7": return cell7;
            case "cell8": return cell8;
            case "cell9": return cell9;
            default: return null;
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
            cell1.setText(""); cell1.setDisable(false);
            cell2.setText(""); cell2.setDisable(false);
            cell3.setText(""); cell3.setDisable(false);
            cell4.setText(""); cell4.setDisable(false);
            cell5.setText(""); cell5.setDisable(false);
            cell6.setText(""); cell6.setDisable(false);
            cell7.setText(""); cell7.setDisable(false);
            cell8.setText(""); cell8.setDisable(false);
            cell9.setText(""); cell9.setDisable(false);
        });
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleCellAction(ActionEvent event) {
        if (!isPlayerTurn) {
        showError("Invalid Move", "It's not your turn!");
        return;
    }

    Button clickedCell = (Button) event.getSource();

    if (!clickedCell.getText().isEmpty()) {
        showError("Invalid Move", "This cell is already occupied!");
        return;
    }

    String cellId = clickedCell.getId();

    clickedCell.setText(playerSymbol);
    clickedCell.setDisable(true);

    try {
        Map<String, String> moveData = new HashMap<>();
        moveData.put("cell", cellId);
        moveData.put("symbol", playerSymbol);

        RequsetModel request = new RequsetModel("makeMove", moveData);
        dos.writeUTF(gson.toJson(request));
        dos.flush();
        System.out.println("[DEBUG] Move sent to server: " + moveData);

        isPlayerTurn = false;

    } catch (IOException ex) {
        showError("Connection Error", "Failed to send the move to the server: " + ex.getMessage());
    }
    }
}
