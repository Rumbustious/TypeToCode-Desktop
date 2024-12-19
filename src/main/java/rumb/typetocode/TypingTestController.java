package rumb.typetocode;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.event.ActionEvent;
import javafx.util.Duration;

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
    private Label timerLabel;

    private Timeline timer;
    private int timeLeft = 60;

    @FXML
    private void handleStartButton(ActionEvent event) {
        // Logic to start the typing test
        textToTypeLabel.setText("""
                class HelloWorld {
                    public static void main(String[] args) {
                        System.out.println("Hello, World!");\s
                    }
                }
                """);
        typingArea.clear();
        typingArea.setEditable(true);

        // Start the timer
        timeLeft = 60;
        updateTimerLabel();
        startTimer();
    }

    @FXML
    private void handleSubmitButton(ActionEvent event) {
        String typedText = typingArea.getText();
        typingArea.clear();
        typingArea.setEditable(false);

        // Stop the timer if it's running
        if (timer != null) {
            timer.stop();
        }
    }

    private void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeLeft--;
            updateTimerLabel();
            if (timeLeft <= 0) {
                timer.stop();
                typingArea.setEditable(false);
                // Optionally, handle the end of the test (e.g., show a message or calculate the score)
                textToTypeLabel.setText("Time's up! Submit your text.");
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void updateTimerLabel() {
        timerLabel.setText("Time Left: " + timeLeft + "s");
    }
}