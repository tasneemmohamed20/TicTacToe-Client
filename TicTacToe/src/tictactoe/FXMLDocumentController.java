package tictactoe;

import java.io.File;
import java.net.URL;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import theGame.GameBoard;
import theGame.XO;
import java.net.URL;

public class FXMLDocumentController implements Initializable {
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
    private MediaView victoryVideo;
    

    private GameBoard gameBoard;
    private Image xImage;
    private Image oImage;
    @FXML
    private Button recordGame;

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
            
            ImageView imageView = new ImageView(gameBoard.isCross() ? xImage : oImage);
            imageView.setFitHeight(80);
            imageView.setFitWidth(80);
            clickedButton.setGraphic(imageView);
            gameBoard.switchPlayer();
            System.out.println(gameBoard.isCross());
            if (gameBoard.isGameOver()) {
                updateScores();
                if (gameBoard.getWining() == XO.B) {
                    showAlert("Game Over", "Give it another try!", AlertType.INFORMATION);
                } else {
                    showAlert("Game Over", gameBoard.getWining() + " wins!", AlertType.INFORMATION);
                }
                showGameOverAlert();
                
                
            }
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
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText("Would you like to play again?");
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setPrefWidth(300);
        dialogPane.setPrefHeight(150);
        dialogPane.setStyle("-fx-background-color: #5A1E76; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        Label content = (Label) dialogPane.lookup(".content");
        content.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        
        // Customize buttons
//        Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
//        okButton.setText("Play Again");
//        
//        okButton.setStyle("-fx-background-color: #E2BE00; -fx-text-fill: white;");
//        
//        Button cancelButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
//        cancelButton.setText("Exit");
//        cancelButton.setStyle("-fx-background-color: #E2BE00; -fx-text-fill: white;");

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
    
    
    private void showAlert(String title, String message, AlertType alertType) {
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