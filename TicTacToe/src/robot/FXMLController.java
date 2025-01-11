/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.media.MediaView;
import theGame.GameBoard;
import theGame.XO;

/**
 * FXML Controller class
 *
 * @author ALANDALUS
 */
public class FXMLController implements Initializable {
    private int scoreX = 0;  
    private int scoreO = 0;  

    @FXML private Label labelPlayerX;
    @FXML private Label labelPlayerO;
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
    @FXML private MediaView victoryVideo;
    

    private GameBoard gameBoard;
    private Image xImage;
    private Image oImage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gameBoard = new GameBoard();
        xImage = new Image(getClass().getResourceAsStream("/assets/x.png"));
        oImage = new Image(getClass().getResourceAsStream("/assets/o.png"));

        victoryVideo = null;
    }

    @FXML
    private void handleCellAction(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        int row = GridPane.getRowIndex(clickedButton) == null ? 0 : GridPane.getRowIndex(clickedButton);
        int col = GridPane.getColumnIndex(clickedButton) == null ? 0 : GridPane.getColumnIndex(clickedButton);

        if (gameBoard.placeXO(row, col)) {
            updateCell(clickedButton, row, col);

            if (!gameBoard.isGameOver() && gameBoard.isAvailMoves()) {
                // AI's turn
                int[] bestMove = gameBoard.findBestMove();
                if (bestMove[0] != -1) {
                    Button aiButton = getButtonFromPosition(bestMove[0], bestMove[1]);
                    gameBoard.placeXO(bestMove[0], bestMove[1]);
                    updateCell(aiButton, bestMove[0], bestMove[1]);
                }
            }

            if (gameBoard.isGameOver()) {
                updateScores();
                if (gameBoard.getWining() == XO.B) {
                    showAlert("Game Over", "Give it another try!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Game Over", gameBoard.getWining() + " wins!", Alert.AlertType.INFORMATION);
                }
                showGameOverAlert();
            }
        }
    }

    private void updateCell(Button button, int row, int col) {
        ImageView imageView = new ImageView(gameBoard.isCross() ? xImage : oImage);
        imageView.setFitHeight(80);
        imageView.setFitWidth(80);
        button.setGraphic(imageView);
        gameBoard.switchPlayer();
    }

    private Button getButtonFromPosition(int row, int col) {
        int cellIndex = row * 3 + col;
        switch (cellIndex) {
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



    private void resetGame() {
        for (Button cell : new Button[]{cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8, cell9}) {
            cell.setGraphic(null);
        }
        gameBoard = new GameBoard();
        labelScoreX.setText(String.valueOf(scoreX));
        labelScoreO.setText(String.valueOf(scoreO));
    }

    private void showGameOverAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText("Would you like to play again?");
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setPrefWidth(300);
        dialogPane.setPrefHeight(150);
        dialogPane.setStyle("-fx-background-color: #5A1E76; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        Label content = (Label) dialogPane.lookup(".content");
        content.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            resetGame();
        }else{
            System.exit(0);
        }
    }

    // private void showVictoryVideo() {
    //     try {
    //         // Create new stage for video
    //         Stage videoStage = new Stage();
    //         videoStage.setTitle("Victory!");
    //         // Load video file directly using Media
    //         String videoPath = getClass().getResource("/assets/bravo.mp4").toExternalForm();
    //         Media media = new Media(videoPath);
    //         MediaPlayer mediaPlayer = new MediaPlayer(media);
    //         MediaView mediaView = new MediaView(mediaPlayer);
    //         // Create scene
    //         StackPane root = new StackPane();
    //         root.getChildren().add(mediaView);
    //         Scene scene = new Scene(root, 640, 480);
    //         videoStage.setScene(scene);
    //         // Configure player
    //         mediaPlayer.setOnEndOfMedia(() -> {
    //             videoStage.close();
    //             mediaPlayer.dispose();
    //         });
    //         mediaPlayer.setStopTime(Duration.seconds(5));
    //         // Show video
    //         videoStage.show();
    //         mediaPlayer.play();
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         Alert alert = new Alert(Alert.AlertType.ERROR);
    //         alert.setTitle("Error");
    //         alert.setContentText("Could not play victory video");
    //         alert.showAndWait();
    //     }
    // }

    private void updateScores() {
        if (gameBoard.getWining() == XO.X) {
            scoreX++;
            labelScoreX.setText(String.valueOf(scoreX));
        } else if (gameBoard.getWining() == XO.O) {
            scoreO++;
            labelScoreO.setText(String.valueOf(scoreO));
        }
    }
    
    
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setPrefWidth(300);
        dialogPane.setPrefHeight(150);
        dialogPane.setStyle("-fx-background-color: #5A1E76; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        Label content = (Label) dialogPane.lookup(".content");
        content.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        alert.showAndWait();
    }
}
