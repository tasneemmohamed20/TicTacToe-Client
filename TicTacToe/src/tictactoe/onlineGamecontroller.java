package tictactoe;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import models.GameModel;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import models.RequsetModel;
import models.ResponsModel;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class onlineGamecontroller {

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

    private String currentPlayer = "X";
    private String[] board = new String[9];
    private boolean gameEnded = false;

    private DataOutputStream dos;
    private DataInputStream dis;
    private Gson gson = new Gson();
    GameModel gameModel;

    private final Image xImage = new Image("/assets/x.png");
    private final Image oImage = new Image("/assets/o.png");

    public void startGame(GameModel game) {
        gameModel = game;
        labelPlayerX.setText(game.getPlayer1());
        labelPlayerO.setText(game.getPlayer2());

        for (int i = 0; i < board.length; i++) {
            board[i] = "";
        }
    }

    public void setDataOutputStream(DataOutputStream dos) {
        this.dos = dos;
    }

    public void setDataInputStream(DataInputStream dis) {
        this.dis = dis;
    }

    @FXML
    private void handleCellAction(ActionEvent event) {
        if (gameEnded) {
            return;
        }

        Button clickedButton = (Button) event.getSource();
        int cellIndex = getCellIndex(clickedButton);

        if (cellIndex >= 0 && cellIndex < 9 && board[cellIndex].isEmpty()) {
            board[cellIndex] = currentPlayer;

            // تعيين الصورة بناءً على اللاعب الحالي
            if (currentPlayer.equals("X")) {
                setCellImage(clickedButton, xImage);
            } else {
                setCellImage(clickedButton, oImage);
            }

            clickedButton.setDisable(true); // تعطيل الزر بعد النقر
            sendMoveToServer(gameModel, board, cellIndex);

            if (checkWinner()) {
                gameEnded = true;
                System.out.println("Player " + currentPlayer + " wins!");
                return;
            }

            if (isDraw()) {
                gameEnded = true;
                System.out.println("It's a draw!");
                return;
            }

            currentPlayer = (currentPlayer.equals("X")) ? "O" : "X"; // تغيير الدور
        } else {
            System.out.println("Invalid move: Cell is already occupied or out of range.");
        }
    }

    private int getCellIndex(Button clickedButton) {
        if (clickedButton == cell1) return 0;
        if (clickedButton == cell2) return 1;
        if (clickedButton == cell3) return 2;
        if (clickedButton == cell4) return 3;
        if (clickedButton == cell5) return 4;
        if (clickedButton == cell6) return 5;
        if (clickedButton == cell7) return 6;
        if (clickedButton == cell8) return 7;
        if (clickedButton == cell9) return 8;
        return -1;
    }

    private boolean checkWinner() {
        // التحقق من الصفوف
        for (int i = 0; i < 3; i++) {
            if (board[i * 3].equals(currentPlayer)
                    && board[i * 3 + 1].equals(currentPlayer)
                    && board[i * 3 + 2].equals(currentPlayer)) {
                return true;
            }
        }

        // التحقق من الأعمدة
        for (int i = 0; i < 3; i++) {
            if (board[i].equals(currentPlayer)
                    && board[i + 3].equals(currentPlayer)
                    && board[i + 6].equals(currentPlayer)) {
                return true;
            }
        }

        // التحقق من القطر الرئيسي
        if (board[0].equals(currentPlayer)
                && board[4].equals(currentPlayer)
                && board[8].equals(currentPlayer)) {
            return true;
        }

        // التحقق من القطر الثانوي
        if (board[2].equals(currentPlayer)
                && board[4].equals(currentPlayer)
                && board[6].equals(currentPlayer)) {
            return true;
        }

        return false;
    }

    private boolean isDraw() {
        for (String cell : board) {
            if (cell.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void sendMoveToServer(GameModel game, String[] board, int cellIndex) {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("move", String.valueOf(cellIndex)); // رقم الحركة
            data.put("player", currentPlayer); // رمز اللاعب (X أو O)

            // إنشاء الكائن الرئيسي
            Map<String, Object> request = new HashMap<>();
            request.put("action", "move"); // نوع الإجراء
            request.put("data", data); // البيانات

            // تحويل الكائن إلى JSON
            String jsonRequest = gson.toJson(request);
            System.out.println("Sent to server: " + jsonRequest);

            // إرسال البيانات إلى الخادم
            dos.writeUTF(jsonRequest);
            dos.flush();
        } catch (IOException ex) {
            System.err.println("Error sending move to server: " + ex.getMessage());
        }
    }

    void handleOpponentMove(Object data) {
        Platform.runLater(() -> {
            Map<String, String> moveData = (Map<String, String>) data;
            System.out.println("moveData: " + moveData);

            // التحقق من وجود "move" و "player" في البيانات
            if (moveData.containsKey("move") && moveData.containsKey("player")) {
                String player = moveData.get("player");
                int position = Integer.parseInt(moveData.get("move"));

                // تحديث اللوحة
                updateBoard(position, player);
            } else {
                System.err.println("Invalid move data received: " + moveData);
            }
        });
    }

    void handleGameOver(Object data) {
        Platform.runLater(() -> {
            Map<String, String> gameOverData = (Map<String, String>) data;
            String winner = gameOverData.get("winner");

            // عرض رسالة نهاية اللعبة
            showGameOverAlert(winner);
        });
    }

    private void updateBoard(int position, String player) {
        Button button = getButtonByPosition(position);
        if (button != null) {
           
            if (player.equals("X")) {
                setCellImage(button, xImage);
            } else {
                setCellImage(button, oImage);
            }
            button.setDisable(true);
        }
    }

    private Button getButtonByPosition(int position) {
        switch (position) {
            case 0: return cell1;
            case 1: return cell2;
            case 2: return cell3;
            case 3: return cell4;
            case 4: return cell5;
            case 5: return cell6;
            case 6: return cell7;
            case 7: return cell8;
            case 8: return cell9;
            default: return null;
        }
    }

    private void showGameOverAlert(String winner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);

        if (winner.equals("Draw")) {
            alert.setContentText("It's a draw!");
        } else {
            alert.setContentText("Player " + winner + " wins!");
        }

        alert.showAndWait();
    }

    private void setCellImage(Button cell, Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(100); // تعيين عرض الصورة
        imageView.setFitHeight(100); // تعيين ارتفاع الصورة
        imageView.setPreserveRatio(true); // الحفاظ على نسبة العرض إلى الارتفاع
        cell.setGraphic(imageView); // تعيين الصورة للزر
    }

    @FXML
    private void handleRecordGame(ActionEvent event) {
        System.out.println("Game recorded!");
    }
}