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

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private final Gson gson = new Gson();
    private String currentPlayer;
    private String opponent;
    private int playerXScore = 0;
    private int playerOScore = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Perform initialization if necessary
    }

    public void startGame(GameModel game) {
        labelPlayerX.setText(game.getPlayer1());
        labelPlayerO.setText(game.getPlayer2());
        currentPlayer = game.getPlayer1();
        opponent = game.getPlayer2();

        connectToServer();
        listenToServer();
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 5005);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            System.out.println("Connected to server.");
        } catch (IOException ex) {
            System.err.println("Failed to connect to server. Please check your connection.");
        }
    }

    private void listenToServer() {
        Thread listener = new Thread(() -> {
            try {
                while (true) {
                    String jsonResponse = dis.readUTF();
                    Platform.runLater(() -> {
                        try {
                            ResponsModel response = gson.fromJson(jsonResponse, ResponsModel.class);
                            handleResponse(response);
                        } catch (Exception e) {
                            System.err.println("Malformed response: " + jsonResponse);
                        }
                    });
                }
            } catch (IOException ex) {
                System.err.println("Server connection lost.");
            }
        });
        listener.setDaemon(true);
        listener.start();
    }

    private void closeConnection() {
        try {
            if (dis != null) dis.close();
            if (dos != null) dos.close();
            if (socket != null) socket.close();
            System.out.println("Connection closed.");
        } catch (IOException ex) {
            System.err.println("Error closing connection.");
        }
    }

    @Override
    public void finalize() throws Throwable {
        closeConnection();
        super.finalize();
    }

    private void handleResponse(ResponsModel response) {
        if (response == null || response.getStatus() == null) {
            System.err.println("Invalid response received.");
            return;
        }

        switch (response.getStatus()) {
            case "move":
                handleMoveResponse(response);
                break;
            case "gameOver":
                handleGameOverResponse(response);
                break;
            case "accept":
                System.out.println("Game request accepted.");
                break;
            case "error":
                System.err.println("Error received: " + response.getMessage());
                break;
            default:
                System.err.println("Unknown response status: " + response.getStatus());
        }
    }

    private void handleMoveResponse(ResponsModel response) {
        try {
            Object rawData = response.getData();
            if (rawData instanceof Map<?, ?>) {
                @SuppressWarnings("unchecked")
                Map<String, String> moveData = (Map<String, String>) rawData;

                String player = moveData.get("player");
                int cellNumber = Integer.parseInt(moveData.get("cellNumber"));

                if (!player.equals(currentPlayer)) {
                    handleOpponentMove(cellNumber);
                }
            } else {
                System.err.println("Invalid move data type.");
            }
        } catch (Exception e) {
            System.err.println("Error processing move response: " + e.getMessage());
        }
    }

    private void handleOpponentMove(int cellNumber) {
        Platform.runLater(() -> {
            Button targetCell = getCellByNumber(cellNumber);
            if (targetCell != null && targetCell.getText().isEmpty()) {
                targetCell.setText(opponent);
                targetCell.setDisable(true);

                if (checkGameOver(opponent)) {
                    System.out.println("Opponent wins!");
                    disableAllCells();
                }
            }
        });
    }

    private void handleGameOverResponse(ResponsModel response) {
        Platform.runLater(() -> {
            try {
                @SuppressWarnings("unchecked")
                Map<String, String> data = gson.fromJson(response.getData().toString(), Map.class);
                String winner = data.get("winner");

                if (winner.equals(labelPlayerX.getText())) {
                    System.out.println("Player X wins!");
                    labelScoreX.setText(String.valueOf(++playerXScore));
                } else if (winner.equals(labelPlayerO.getText())) {
                    System.out.println("Player O wins!");
                    labelScoreO.setText(String.valueOf(++playerOScore));
                } else {
                    System.out.println("It's a draw!");
                }
                disableAllCells();
            } catch (Exception e) {
                System.err.println("Error processing game over response: " + e.getMessage());
            }
        });
    }

    private Button getCellByNumber(int number) {
        switch (number) {
            case 1: return cell1;
            case 2: return cell2;
            case 3: return cell3;
            case 4: return cell4;
            case 5: return cell5;
            case 6: return cell6;
            case 7: return cell7;
            case 8: return cell8;
            case 9: return cell9;
            default: return null;
        }
    }

    private boolean checkGameOver(String symbol) {
        int[][] winCombos = {
            {1, 2, 3}, {4, 5, 6}, {7, 8, 9},
            {1, 4, 7}, {2, 5, 8}, {3, 6, 9},
            {1, 5, 9}, {3, 5, 7}
        };

        for (int[] combo : winCombos) {
            if (getCellByNumber(combo[0]).getText().equals(symbol) &&
                getCellByNumber(combo[1]).getText().equals(symbol) &&
                getCellByNumber(combo[2]).getText().equals(symbol)) {
                return true;
            }
        }
        return false;
    }

    private void disableAllCells() {
        Platform.runLater(() -> {
            cell1.setDisable(true);
            cell2.setDisable(true);
            cell3.setDisable(true);
            cell4.setDisable(true);
            cell5.setDisable(true);
            cell6.setDisable(true);
            cell7.setDisable(true);
            cell8.setDisable(true);
            cell9.setDisable(true);
        });
    }

    @FXML
    private void handleCellAction(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        if (clickedButton.getText().isEmpty()) {
            clickedButton.setText(currentPlayer);
            int cellNumber = getCellNumber(clickedButton);
            sendMoveToServer(cellNumber);
        }
    }

    private int getCellNumber(Button cell) {
        if (cell == cell1) return 1;
        if (cell == cell2) return 2;
        if (cell == cell3) return 3;
        if (cell == cell4) return 4;
        if (cell == cell5) return 5;
        if (cell == cell6) return 6;
        if (cell == cell7) return 7;
        if (cell == cell8) return 8;
        if (cell == cell9) return 9;
        return -1;
    }

   private void sendMoveToServer(int cellNumber) {
    try {
        if (cellNumber != -1 && currentPlayer != null && !currentPlayer.isEmpty()) {
            Map<String, String> moveData = new HashMap<>();
            moveData.put("player", currentPlayer);
            moveData.put("cell", String.valueOf(cellNumber));

            Map<String, Object> request = new HashMap<>();
            request.put("action", "move");
            request.put("data", moveData);

            String jsonRequest = gson.toJson(request);
            System.out.println("Sending to server: " + jsonRequest);

            dos.writeUTF(jsonRequest);
            dos.flush();
        } else {
            System.err.println("Invalid move data: currentPlayer is null/empty or cellNumber is invalid.");
        }
    } catch (IOException ex) {
        System.err.println("Failed to send move to server: " + ex.getMessage());
    }
}


}
