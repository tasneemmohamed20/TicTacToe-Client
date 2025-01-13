/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

/**
 * FXML Controller class
 *
 * @author El-Wattaneya
 */
public class VideoLayoutController implements Initializable {

    @FXML
    private MediaView mediaView;
    @FXML
    private Button newGame;
    @FXML
    private Button close;

    private MediaPlayer mediaPlayer;
private Runnable onNewGameAction;
    
    public void initialize() {
    try {
        String videoPath = getClass().getResource("/assets/bravo.mp4").toExternalForm();
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

        mediaPlayer.stop();
        System.exit(0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
