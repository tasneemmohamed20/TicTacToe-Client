/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EasyMode;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import tictactoe.LoginController;

/**
 * FXML Controller class
 *
 * @author El-Wattaneya
 */
public class EasyModeController implements Initializable {

    @FXML
    private Button cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8, cell9;

    @FXML
    private Label labelScoreX, labelScoreO;

    private boolean isPlayerTurn = true; 
    private int scoreX = 0, scoreO = 0;

    @FXML
    public void handleCellAction(javafx.event.ActionEvent event) {
        Button clickedButton = (Button) event.getSource();

        if (clickedButton.getText().isEmpty() && isPlayerTurn) {
            clickedButton.setText("X");
            isPlayerTurn = false;

            if (checkWin("X")) {
                scoreX++;
                labelScoreX.setText(String.valueOf(scoreX));
                resetBoard();
                isPlayerTurn = true; 
                return;
            }

            if (!isBoardFull()) {
                computerMove();
                if (checkWin("O")) {
                    scoreO++;
                    labelScoreO.setText(String.valueOf(scoreO));
                    resetBoard();
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
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @FXML
    private void handleBackButton(ActionEvent event) {
        new LoginController().navigateToScreen(event, "Menu.fxml", "Menu");
    }
}
