/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author El-Wattaneya
 */
public class VideoLayoutController implements Initializable {

    @FXML
    private Label winnerLabel;
    @FXML
    private MediaView mediaView;
    @FXML
    private Button newGame;
    @FXML
    private Button close;

    private MediaPlayer mediaPlayer;
    private Runnable onNewGameAction;

    public void initialize(String path) {
        try {
            String videoPath = getClass().getResource(path).toExternalForm();
            if (videoPath != null) {
                Media media = new Media(videoPath);
                mediaPlayer = new MediaPlayer(media);
                mediaView.setMediaPlayer(mediaPlayer);
                mediaPlayer.play();

            } else {
                System.out.println("Video path is null. Check the resource path.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnNewGameAction(Runnable onNewGameAction) {
        this.onNewGameAction = onNewGameAction;
    }

    @FXML
    private void onNewGameClicked(ActionEvent event) {
        if (onNewGameAction != null) {
            mediaPlayer.stop();
            onNewGameAction.run();
        } else {
            System.out.println("No action defined for New Game.");
        }
    }

    @FXML
    private void onCloseClicked(ActionEvent event) {

        try {
            mediaPlayer.stop();
            ((Button) event.getSource()).getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Parent levelsRoot = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene levelsScene = new Scene(levelsRoot);
            stage.setScene(levelsScene);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(VideoLayoutController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setWinnerText(String text) {
        winnerLabel.setText(text);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
