package rumb.typetocode;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.event.ActionEvent;

public class TypingTestController {

    @FXML
    private Label textToTypeLabel;

    @FXML
    private TextArea typingArea;

    @FXML
    private Button startButton;

    @FXML
    private Button submitButton;

    @FXML
    private void handleStartButton(ActionEvent event) {
        // Logic to start the typing test
        textToTypeLabel.setText("New text to type...");
        typingArea.clear();
        typingArea.setEditable(true);
    }

    @FXML
    private void handleSubmitButton(ActionEvent event) {
        String typedText = typingArea.getText();

        typingArea.setEditable(false);
    }
}
