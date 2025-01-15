package tictactoe;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;

public abstract class FXMLDocumentBase extends BorderPane {

    protected final FlowPane flowPane;
    protected final Button button;
    protected final Button button0;
    protected final AnchorPane anchorPane;
    protected final Text text;
    protected final TextField textField;
    protected final TextField textField0;
    protected final Text text0;

    public FXMLDocumentBase() {

        flowPane = new FlowPane();
        button = new Button();
        button0 = new Button();
        anchorPane = new AnchorPane();
        text = new Text();
        textField = new TextField();
        textField0 = new TextField();
        text0 = new Text();

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

        button.setMnemonicParsing(false);
        button.setText("login");

        button0.setMnemonicParsing(false);
        button0.setText("signup");
        FlowPane.setMargin(button0, new Insets(0.0));
        setBottom(flowPane);

        BorderPane.setAlignment(anchorPane, javafx.geometry.Pos.CENTER);
        anchorPane.setPrefHeight(200.0);
        anchorPane.setPrefWidth(200.0);

        text.setLayoutX(208.0);
        text.setLayoutY(75.0);
        text.setStrokeType(javafx.scene.shape.StrokeType.OUTSIDE);
        text.setStrokeWidth(0.0);
        text.setText("Username:");
        text.setWrappingWidth(103.98307228088379);

        textField.setLayoutX(208.0);
        textField.setLayoutY(90.0);

        textField0.setLayoutX(210.0);
        textField0.setLayoutY(195.0);

        text0.setLayoutX(208.0);
        text0.setLayoutY(191.0);
        text0.setStrokeType(javafx.scene.shape.StrokeType.OUTSIDE);
        text0.setStrokeWidth(0.0);
        text0.setText("Password:");
        text0.setWrappingWidth(79.98307228088379);
        setCenter(anchorPane);

        flowPane.getChildren().add(button);
        flowPane.getChildren().add(button0);
        anchorPane.getChildren().add(text);
        anchorPane.getChildren().add(textField);
        anchorPane.getChildren().add(textField0);
        anchorPane.getChildren().add(text0);

    }
}
