/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import robot.HardMode.Move;
import static robot.HardMode.evaluate;
import static robot.HardMode.findBestMove;
import static robot.HardMode.isMoveLeft;
import tictactoe.LoginController;
import tictactoe.VideoLayoutController;

/**
 * FXML Controller class
 *
 * @author ALANDALUS
 */
public class FXMLController implements Initializable { 

    @FXML private Label labelScoreX;
    @FXML private Label labelScoreO;
    @FXML private Button cell1;
    @FXML private Button cell2;
    @FXML private Button cell3;
    @FXML private Button cell4;
    @FXML private Button cell5;
    @FXML private Button cell6;
    @FXML private Button cell7;
    @FXML private Button cell8;
    @FXML private Button cell9;
  
    private boolean winner = false;  
    int moveNum = 0;
    Move bestMove;
    public Button[][] board=new Button[3][3];
    private final Image xImage = new Image("/assets/x.png"); 
    private final Image oImage = new Image("/assets/o.png"); 
    private int scoreX = 0;  
    private int scoreO = 0; 
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
   
        board[0][0] = cell1;
        board[0][1] = cell2;
        board[0][2] = cell3;
        board[1][0] = cell4;
        board[1][1] = cell5;
        board[1][2] = cell6;
        board[2][0] = cell7;
        board[2][1] = cell8;
        board[2][2] = cell9;

        for (Button[] btns : board) {
            for (Button btn : btns) {
                btn.addEventHandler(ActionEvent.ACTION, (ActionEvent event) -> {
                     if(!winner){
                       setCellImage(btn, xImage);
                        btn.setText("X");   
                    btn.setMouseTransparent(true);
                    if (moveNum + 1 < 9) {
                        bestMove = findBestMove(board);
                        setCellImage(board[bestMove.row][bestMove.col], oImage);
                        board[bestMove.row][bestMove.col].setText("O");
                        board[bestMove.row][bestMove.col].setMouseTransparent(true);
                    }
                    moveNum += 2;
                    if (moveNum >= 5) {

                        int result = evaluate(board);
                        if (result == 10) {
                            System.out.println("You lost.");
                            showVideoAlert("You lost.", "/assets/looser.mp4");
                            winner=true;
                            scoreO++;
                        } else if (result == -10) {
                            System.out.println("You won.");
                            scoreX+=1;
                            showVideoAlert("You lost.", "/assets/bravo.mp4");
                            winner=true;
                        } else if (isMoveLeft(board) == false) {
                            System.out.println("No One Wins.");
                            showVideoAlert("No One Wins.", "/assets/draw.mp4");
                            winner=true;
                        }
                    }}
                });
            }
        }
    } 
    
    private void setCellImage(Button cell, Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(100); 
        imageView.setFitHeight(100); 
        imageView.setPreserveRatio(true);
        cell.setGraphic(imageView); 
    }
    
    @FXML
    private void handleCellAction(ActionEvent event) {}
    
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
        } catch (IOException e) {
            System.out.println("robot.FXMLController" + e.toString());
        }
    }
    
    private void resetBoard() {
        for (Button[] btns : board) {
            for (Button btn : btns) {
                btn.setText("");
                btn.setGraphic(null); 
                btn.setMouseTransparent(false);
            }
        }
        moveNum = 0;
        winner = false;
        labelScoreX.setText(String.valueOf(scoreX));
        labelScoreO.setText(String.valueOf(scoreO));
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        new LoginController().navigateToScreen(event, "Levels.fxml", "Levels");
    }
}
