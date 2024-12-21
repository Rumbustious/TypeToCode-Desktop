package rumb.typetocode;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.event.ActionEvent;
import javafx.util.Duration;

public class TypingTestController {

    @FXML
    private Label textToTypeLabel;

    @FXML
    private TextFlow textFlow;

    @FXML
    private TextArea typingArea;

    @FXML
    private Button startButton;

    @FXML
    private Label timerLabel;

    private Timeline timer;
    private int timeLeft = 60;
    private String textToType;
    private int correctChars = 0;
    private int totalChars = 0;
    private int realCorrectChars = 0;

    @FXML
    private void handleStartButton(ActionEvent event) {
        // Logic to start the typing test
        textToType = """
                class HelloWorld {
                    public static void main(String[] args) {
                        System.out.println("Hello, World!");\s
                    }
                }
                """;
        textToTypeLabel.setText(textToType);
        typingArea.clear();
        typingArea.setEditable(true);
        typingArea.addEventFilter(KeyEvent.KEY_TYPED, this::handleTyping);

        // Initialize the text flow with the text to type
        textFlow.getChildren().clear();
        for (char c : textToType.toCharArray()) {
            Text text = new Text(String.valueOf(c));
            textFlow.getChildren().add(text);
        }

        // Start the timer
        timeLeft = 60;
        correctChars = 0;
        totalChars = 0;
        realCorrectChars = 0;
        updateTimerLabel();
        startTimer();
    }

    private void handleTyping(KeyEvent event) {
        String typedText = typingArea.getText();
        int currentIndex = typedText.length() - 1;

        if (currentIndex >= 0 && currentIndex < textToType.length()) {
            char expectedChar = textToType.charAt(currentIndex);
            char typedChar = event.getCharacter().charAt(0);

            // Handle spaces and tabs
            if (typedChar == '\t') {
                typedChar = ' ';
            }

            Text text = (Text) textFlow.getChildren().get(currentIndex);
            if (typedChar == expectedChar) {
                text.setStyle("-fx-fill: green;");
                correctChars++;
                realCorrectChars++;
            } else {
                text.setStyle("-fx-fill: red;");
                realCorrectChars--;
            }
            totalChars++;
        }

        if (typedText.equals(textToType)) {
            endTest();
        }

        // Move the caret to the end of the text
        typingArea.positionCaret(typedText.length());
    }

    private void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeLeft--;
            updateTimerLabel();
            if (timeLeft <= 0) {
                endTest();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void updateTimerLabel() {
        if (timeLeft <= 10) {
            timerLabel.setStyle("-fx-text-fill: red;");
        } else {
            timerLabel.setStyle("-fx-text-fill: green;");
        }
        timerLabel.setText("Time Left: " + timeLeft + "s");
    }

    private void endTest() {
        if (timer != null) {
            timer.stop();
        }
        typingArea.setEditable(false);
        typingArea.removeEventFilter(KeyEvent.KEY_TYPED, this::handleTyping);

        double wpm = (double) correctChars / 5 / 60 * 60; // words per minute
        double relativeAccuracy = (double) correctChars / totalChars * 100;
        double realAccuracy = (double) realCorrectChars / totalChars * 100;

        textToTypeLabel.setText(String.format("Test finished! Speed: %.2f WPM, Relative Accuracy: %.2f%%, Real Accuracy: %.2f%%", wpm, relativeAccuracy, realAccuracy));
    }
}