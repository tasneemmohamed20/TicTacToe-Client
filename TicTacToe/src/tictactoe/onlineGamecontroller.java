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
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import models.GameModel;
import models.GameRecord;
import models.Move;
import models.RequsetModel;
import models.ResponsModel;

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
    boolean isRecording = false;
    GameRecord record;
    private final Image xImage = new Image("/assets/x.png");
    private final Image oImage = new Image("/assets/o.png");
    String userName;
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

        this.currentPlayer = game.getPlayer1();
        this.playerSymbol = game.getCurrentPlayer().equals(game.getPlayer1())
                ? game.getPlayer1Symbol() : game.getPlayer2Symbol();
        this.opponentSymbol = game.getCurrentPlayer().equals(game.getPlayer1())
                ? game.getPlayer2Symbol() : game.getPlayer1Symbol();
        this.gameId = game.getGameId();

        Platform.runLater(() -> {
            labelPlayerX.setText(game.getPlayer1());
            labelPlayerO.setText(game.getPlayer2());
            labelScoreX.setText("0");
            labelScoreO.setText("0");
            resetBoard();
        });

        isPlayerTurn = game.getCurrentPlayer().equals(currentPlayer);
        System.out.println("Game started successfully. Game ID: " + gameId);
    }

    void initializeGameUI(GameModel game) {
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
        new Thread(() -> {
            while (true) {
                try {
                    String responseString = dis.readUTF();
                    ResponsModel response = gson.fromJson(responseString, ResponsModel.class);
                    Platform.runLater(() -> handleServerResponse(response));
                } catch (IOException | JsonSyntaxException ex) {
                    Platform.runLater(() -> showError("Connection Error",
                            "Disconnected from the server: " + ex.getMessage()));
                    break;
                }
            }
        }).start();
    }

    private void handleServerResponse(ResponsModel response) {
        if (response == null) {
            System.out.println("Received an empty or null response.");
            return;
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

                   showVideoAlert("gameOver", "/assets/bravo.mp4"); 
                
                //handleGameOver(response.getMessage(), winner);
                break;

            case "info":
                System.out.println("Info message: " + response.getMessage());
                Platform.runLater(() -> {
                    if (response.getMessage().contains("Your turn")) {
                        isPlayerTurn = true;
                    }
                });
                break;

            case "error":
                showError("Server Error", response.getMessage());
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
                                        //System.out.println("File saved to: " + new File("rec.txt").getAbsolutePath());

                                    }
                                }
                            }

                            isPlayerTurn = currentTurn.equals(playerSymbol);
                            System.out.println("[DEBUG] Board updated. Current turn: " + currentTurn
                                    + ", Player symbol: " + playerSymbol
                                    + ", isPlayerTurn: " + isPlayerTurn);
                        });
                    } catch (Exception e) {
                        System.err.println("Error parsing update data: " + e.getMessage());
                    }
                }
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

    private void showVideoAlert(String title, String videoPath) {
        try {

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
