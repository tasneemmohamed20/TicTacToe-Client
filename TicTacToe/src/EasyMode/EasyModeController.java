package EasyMode;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
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
import tictactoe.LoginController;
import tictactoe.VideoLayoutController;

public class EasyModeController implements Initializable {

    @FXML
    private Button cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8, cell9;

    @FXML
    private Label labelScoreX, labelScoreO;

    private boolean isPlayerTurn = true;
    private int scoreX = 0, scoreO = 0;

    private final Image xImage = new Image("/assets/x.png"); 
    private final Image oImage = new Image("/assets/o.png"); 

    @FXML
    public void handleCellAction(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();

        if (clickedButton.getText().isEmpty() && isPlayerTurn) {
            makePlayerMove(clickedButton);
        }
    }

    private void makePlayerMove(Button clickedButton) {
        setCellImage(clickedButton, xImage);
        clickedButton.setText("X");
        
        if (checkWin("X")) {
            handleGameResult("X", true);
            return;
        }

        if (!isBoardFull()) {
            isPlayerTurn = false;
            computerMove();
            
            if (checkWin("O")) {
                handleGameResult("O", false);
            }
        }

        checkGameStatus();
        isPlayerTurn = true;
    }

    private void handleGameResult(String winner, boolean isPlayerWin) {
        if (isPlayerWin) {
            scoreX++;
            labelScoreX.setText(String.valueOf(scoreX));
            showVideoAlert("X" + " wins!", "/assets/bravo.mp4");
        } else {
            scoreO++;
            labelScoreO.setText(String.valueOf(scoreO));
            showVideoAlert("Computer" + " wins!", "/assets/looser.mp4");
        }
        
        lockBoard();
    }

    private void computerMove() {
        Button blockingMove = findBlockingMove("X");
        if (blockingMove != null) {
            makeComputerMove(blockingMove);
        } else {
            List<Button> emptyCells = getEmptyCells();
            if (!emptyCells.isEmpty()) {
                Random random = new Random();
                Button chosenCell = emptyCells.get(random.nextInt(emptyCells.size()));
                makeComputerMove(chosenCell);
            }
        }
    }

    private Button findBlockingMove(String playerSymbol) {
        Button[][] board = {
            {cell1, cell2, cell3},
            {cell4, cell5, cell6},
            {cell7, cell8, cell9}
        };

        // Check rows, columns, and diagonals for potential blocking
        for (int i = 0; i < 3; i++) {
            // Check rows
            if (countSymbol(board[i][0], board[i][1], board[i][2], playerSymbol) == 2) {
                return getEmptyCell(board[i][0], board[i][1], board[i][2]);
            }
            
            // Check columns
            if (countSymbol(board[0][i], board[1][i], board[2][i], playerSymbol) == 2) {
                return getEmptyCell(board[0][i], board[1][i], board[2][i]);
            }
        }

        // Check diagonals
        if (countSymbol(board[0][0], board[1][1], board[2][2], playerSymbol) == 2) {
            return getEmptyCell(board[0][0], board[1][1], board[2][2]);
        }
        if (countSymbol(board[0][2], board[1][1], board[2][0], playerSymbol) == 2) {
            return getEmptyCell(board[0][2], board[1][1], board[2][0]);
        }

        return null;
    }

    private int countSymbol(Button b1, Button b2, Button b3, String symbol) {
        int count = 0;
        if (b1.getText().equals(symbol)) count++;
        if (b2.getText().equals(symbol)) count++;
        if (b3.getText().equals(symbol)) count++;
        return count;
    }

    private Button getEmptyCell(Button b1, Button b2, Button b3) {
        if (b1.getText().isEmpty()) return b1;
        if (b2.getText().isEmpty()) return b2;
        if (b3.getText().isEmpty()) return b3;
        return null;
    }

    private void makeComputerMove(Button chosenCell) {
        setCellImage(chosenCell, oImage);
        chosenCell.setText("O");
    }

    private void checkGameStatus() {
        if (isBoardFull() && !checkWin("X") && !checkWin("O")) {
            showVideoAlert("Draw!", "/assets/draw.mp4");
            resetBoard();
        }
    }

    private List<Button> getEmptyCells() {
        List<Button> emptyCells = new ArrayList<>();
        Button[] cells = {cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8, cell9};

        for (Button cell : cells) {
            if (cell.getText().isEmpty()) {
                emptyCells.add(cell);
            }
        }
        return emptyCells;
    }

    private void setCellImage(Button cell, Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(100); 
        imageView.setFitHeight(100); 
        imageView.setPreserveRatio(true);
        cell.setGraphic(imageView); 
    }

    private boolean checkWin(String player) {
        Button[][] board = {
            {cell1, cell2, cell3},
            {cell4, cell5, cell6},
            {cell7, cell8, cell9}
        };

        for (int i = 0; i < 3; i++) {
            if (board[i][0].getText().equals(player) && 
                board[i][1].getText().equals(player) && 
                board[i][2].getText().equals(player)) {
                return true;
            }
            if (board[0][i].getText().equals(player) && 
                board[1][i].getText().equals(player) && 
                board[2][i].getText().equals(player)) {
                return true;
            }
        }

        if (board[0][0].getText().equals(player) && 
            board[1][1].getText().equals(player) && 
            board[2][2].getText().equals(player)) {
            return true;
        }
        if (board[0][2].getText().equals(player) && 
            board[1][1].getText().equals(player) && 
            board[2][0].getText().equals(player)) {
            return true;
        }

        return false;
    }

    private boolean isBoardFull() {
        return getEmptyCells().isEmpty();
    }

    private void lockBoard() {
        Button[] cells = {cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8, cell9};
        for (Button cell : cells) {
            cell.setDisable(true);
        }
    }

    private void resetBoard() {
        Button[] cells = {cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8, cell9};
        for (Button cell : cells) {
            cell.setText("");
            cell.setDisable(false);
            cell.setGraphic(null); 
        }
        
        labelScoreX.setText(String.valueOf(scoreX));
        labelScoreO.setText(String.valueOf(scoreO));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        new LoginController().navigateToScreen(event, "Menu.fxml", "Menu");
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
                resetBoard();
                stage.close();
            });

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Game Error");
            alert.setContentText("Could not display game result: " + e.getMessage());
            alert.showAndWait();
            resetBoard();
        }
    }
}