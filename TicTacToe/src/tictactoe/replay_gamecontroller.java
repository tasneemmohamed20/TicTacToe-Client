/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.GameRecord;
import models.Move;

/**
 * FXML Controller class
 *
 * @author HP
 */
public class replay_gamecontroller implements Initializable {

    @FXML
    private Label labelPlayerX;
    @FXML
    private Label labelScoreX;
    @FXML
    private Label labelPlayerO;
    @FXML
    private Label labelScoreO;
    @FXML
    private Button cell1;
    @FXML
    private Button cell2;
    @FXML
    private Button cell3;
    @FXML
    private Button cell4;
    @FXML
    private Button cell5;
    @FXML
    private Button cell6;
    @FXML
    private Button cell7;
    @FXML
    private Button cell8;
    @FXML
    private Button cell9;
    private String recordName;
    private Image xImage;
    private Image oImage;
    String userName;

    public void setUserName(String userName) {
        this.userName = userName;
    }
    /**
     * Initializes the controller class.
     */
    private boolean isInitialized = false;

    public void setRecordName(String recName) {
        this.recordName = recName;
        System.out.println("Record name set: " + recName);

        // If initialize() has already run, load the file now
        if (isInitialized) {
            loadRecord();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isInitialized = true;

        if (recordName != null && !recordName.trim().isEmpty()) {
            loadRecord();
        } else {
            System.err.println("Error: Record name is null or empty.");
        }
        xImage = new Image(getClass().getResourceAsStream("/assets/x.png"));
        oImage = new Image(getClass().getResourceAsStream("/assets/o.png"));
    }

    private void loadRecord() {
        if (recordName == null || recordName.trim().isEmpty()) {
            System.err.println("Cannot load record: record name is null or empty.");
            return;
        }
        GameRecord recGame = new GameRecord(userName + ".txt");
        List<Move> moves = recGame.readRecord(recordName);

        Thread t = new Thread(() -> {
            try {
                Thread.sleep(1000);
                for (Move move : moves) {
                    System.out.println(move.getPlayer());
                    Button cellBtn = getCellById(move.getCellId());
                    Platform.runLater(() -> {
                        ImageView imageView = new ImageView(move.getPlayer().equals("X") ? xImage : oImage);
                        imageView.setFitHeight(80);
                        imageView.setFitWidth(80);
                        cellBtn.setGraphic(imageView);
                    });
                    
                    Thread.sleep(1000);
               
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(replay_gamecontroller.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        t.start();
    }

    @FXML
    private void handleCellAction(ActionEvent event) {
    }

    private Button getCellById(String cellId) {
        switch (cellId) {
            case "cell1": return cell1;
            case "cell2": return cell2;
            case "cell3": return cell3;
            case "cell4": return cell4;
            case "cell5": return cell5;
            case "cell6": return cell6;
            case "cell7": return cell7;
            case "cell8": return cell8;
            case "cell9": return cell9;
            default: return null;
        }
    }

}
