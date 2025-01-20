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
            
            setCellImage(clickedButton, xImage);
            clickedButton.setText("X");
            isPlayerTurn = false;

            if (checkWin("X")) {
                scoreX++;
                labelScoreX.setText(String.valueOf(scoreX));
                //resetBoard();
                showVideoAlert("X" + " wins!", "/assets/bravo.mp4");
                isPlayerTurn = true;
                return;
            }

            if (!isBoardFull()) {
                computerMove();
                if (checkWin("O")) {
                    scoreO++;
                    labelScoreO.setText(String.valueOf(scoreO));
                    showVideoAlert("computer" + " wins!", "/assets/looser.mp4");
                }
            }
            isPlayerTurn = true;
        }
    }

    private void computerMove() {
        List<Button> emptyCells = getEmptyCells();
        if (!emptyCells.isEmpty()) {
            Random random = new Random();
            Button chosenCell = emptyCells.get(random.nextInt(emptyCells.size()));
            setCellImage(chosenCell, oImage);
            chosenCell.setText("O");
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
            if (board[i][0].getText().equals(player) && board[i][1].getText().equals(player) && board[i][2].getText().equals(player)) {
                return true;
            }
            if (board[0][i].getText().equals(player) && board[1][i].getText().equals(player) && board[2][i].getText().equals(player)) {
                return true;
            }
        }

        if (board[0][0].getText().equals(player) && board[1][1].getText().equals(player) && board[2][2].getText().equals(player)) {
            return true;
        }
        if (board[0][2].getText().equals(player) && board[1][1].getText().equals(player) && board[2][0].getText().equals(player)) {
            return true;
        }

        return false;
    }

    private boolean isBoardFull() {
        return getEmptyCells().isEmpty();
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
                System.out.println("Starting a new game...");
                resetBoard();
                stage.close();
            });

        } catch (Exception e) {
            e.printStackTrace();
            //showAlert("Error", "Could not load or play video!", Alert.AlertType.ERROR);
        }
    }
}
