package tictactoe;

import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;

public abstract class FXMLDocumentBaseMenu extends BorderPane {

    protected final FlowPane flowPane;
    protected final AnchorPane anchorPane;
    protected final AnchorPane anchorPane0;
    protected final Button button;
    protected final Button button0;
    protected final Button button1;
    protected final Text text;

    public FXMLDocumentBaseMenu() {

        flowPane = new FlowPane();
        anchorPane = new AnchorPane();
        anchorPane0 = new AnchorPane();
        button = new Button();
        button0 = new Button();
        button1 = new Button();
        text = new Text();

        setMaxHeight(USE_PREF_SIZE);
        setMaxWidth(USE_PREF_SIZE);
        setMinHeight(USE_PREF_SIZE);
        setMinWidth(USE_PREF_SIZE);
        setPrefHeight(400.0);
        setPrefWidth(600.0);

        BorderPane.setAlignment(flowPane, javafx.geometry.Pos.CENTER);
        flowPane.setAlignment(javafx.geometry.Pos.CENTER);
        flowPane.setPrefHeight(69.0);
        flowPane.setPrefWidth(600.0);
        setBottom(flowPane);

        BorderPane.setAlignment(anchorPane, javafx.geometry.Pos.CENTER);
        anchorPane.setPrefHeight(200.0);
        anchorPane.setPrefWidth(200.0);

        anchorPane0.setLayoutX(200.0);
        anchorPane0.setLayoutY(120.0);
        anchorPane0.setPrefHeight(251.0);
        anchorPane0.setPrefWidth(200.0);

        button.setLayoutX(74.0);
        button.setLayoutY(59.0);
        button.setMnemonicParsing(false);
        button.setText("Online");

        button0.setLayoutX(76.0);
        button0.setLayoutY(113.0);
        button0.setMnemonicParsing(false);
        button0.setText("Guest");

        button1.setLayoutX(78.0);
        button1.setLayoutY(164.0);
        button1.setMnemonicParsing(false);
        button1.setText("Local");

        text.setLayoutX(281.0);
        text.setLayoutY(116.0);
        text.setStrokeType(javafx.scene.shape.StrokeType.OUTSIDE);
        text.setStrokeWidth(0.0);
        text.setText("Menu");
        text.setWrappingWidth(186.13671875);
        setTop(anchorPane);

        anchorPane0.getChildren().add(button);
        anchorPane0.getChildren().add(button0);
        anchorPane0.getChildren().add(button1);
        anchorPane.getChildren().add(anchorPane0);
        anchorPane.getChildren().add(text);

    }
}
