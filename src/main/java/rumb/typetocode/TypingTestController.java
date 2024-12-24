package rumb.typetocode;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;
import java.util.prefs.Preferences;

public class TypingTestController {
    @FXML private TextFlow displayText;
    @FXML private TextArea typingArea;
    @FXML private Label timerLabel;
    @FXML private Button startButton;

    private Timeline timer;
    private int timeLeft;
    private String textToType;
    private int currentPosition;
    private int errorCount;
    private static final int MAX_ERRORS = 10;
    private boolean testActive;
    private int correctKeystrokes;
    private int totalKeystrokes;
    private String wrongText;
    private long startTime;
    private int consecutiveErrors;

    @FXML
    private void initialize() {
        typingArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                event.consume();
                handleTabPress();
            }
        });

        typingArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (testActive) {
                handleTyping(oldValue, newValue);
            }
        });

        // Set initial timer label based on saved preference
        timeLeft = Preferences.userRoot().node("typetocode").getInt("testDuration", 60);
        updateTimerLabel();

        // Show initial message in displayText
        displayInitialMessage();
    }

    private void displayInitialMessage() {
        displayText.getChildren().clear();
        Text initialMessage = new Text("Press the Start button to begin the test.");
        initialMessage.getStyleClass().add("initial-message");
        displayText.getChildren().add(initialMessage);
    }

    @FXML
    private void handleStartButton(ActionEvent event) {
        testActive = true;
        currentPosition = 0;
        errorCount = 0;
        totalKeystrokes = 0;
        correctKeystrokes = 0;
        wrongText = "";
        startTime = System.currentTimeMillis();
        consecutiveErrors = 0;

        textToType = TestData.getRandomExample();
        timeLeft = Preferences.userRoot().node("typetocode").getInt("testDuration", 60);

        typingArea.clear();
        typingArea.setEditable(true);
        updateDisplayText();
        startTimer();
        Platform.runLater(() -> typingArea.requestFocus());
    }

    private void handleTyping(String oldValue, String newValue) {
        if (!testActive) return;

        totalKeystrokes++;

        if (newValue.length() < oldValue.length()) {
            if (!wrongText.isEmpty()) {
                wrongText = wrongText.substring(0, wrongText.length() - 1);
                if (wrongText.isEmpty()) {
                    typingArea.setStyle("-fx-text-fill: green;");
                }
            } else {
                currentPosition--;
            }
            consecutiveErrors = Math.max(0, consecutiveErrors - 1);
            updateDisplayText();
            return;
        }

        if (currentPosition < textToType.length() && newValue.length() > oldValue.length()) {
            char expectedChar = textToType.charAt(currentPosition);
            char typedChar = newValue.charAt(newValue.length() - 1);

            if (!wrongText.isEmpty()) {
                wrongText += typedChar;
                typingArea.setStyle("-fx-text-fill: red;");
                consecutiveErrors++;
                if (consecutiveErrors >= MAX_ERRORS) {
                    typingArea.setText(oldValue); // Revert the change
                }
                updateDisplayText();
                return;
            }

            if (typedChar != expectedChar) {
                wrongText += typedChar;
                errorCount++;
                typingArea.setStyle("-fx-text-fill: red;");
                consecutiveErrors++;
                if (consecutiveErrors >= MAX_ERRORS) {
                    typingArea.setText(oldValue); // Revert the change
                }
                updateDisplayText();
                return;
            }

            correctKeystrokes++;
            currentPosition++;
            typingArea.setStyle("-fx-text-fill: green;");
            consecutiveErrors = 0;
            updateDisplayText();

            if (currentPosition >= textToType.length()) {
                endTest();
            }
        }
    }

    private void updateDisplayText() {
        displayText.getChildren().clear();

        String completed = textToType.substring(0, currentPosition);
        String current = currentPosition < textToType.length() ? 
                        String.valueOf(textToType.charAt(currentPosition)) : "";
        String remaining = currentPosition < textToType.length() - 1 ? 
                          textToType.substring(currentPosition + 1) : "";

        if (!completed.isEmpty()) {
            Text completedText = new Text(completed);
            completedText.getStyleClass().addAll("typing-text", "completed-text");
            displayText.getChildren().add(completedText);
        }

        if (!current.isEmpty()) {
            Text cursorText = new Text(current);
            cursorText.getStyleClass().addAll(
                "typing-text",
                "cursor",
                wrongText.isEmpty() ? "cursor-active" : "cursor-error"
            );
            displayText.getChildren().add(cursorText);
        }

        if (!remaining.isEmpty()) {
            Text remainingText = new Text(remaining);
            remainingText.getStyleClass().add("typing-text");
            displayText.getChildren().add(remainingText);
        }
    }

    private void handleTabPress() {
        if (!testActive || !wrongText.isEmpty()) return;

        totalKeystrokes++;

        if (currentPosition + 4 <= textToType.length() && 
            textToType.substring(currentPosition, currentPosition + 4).equals("    ")) {
            currentPosition += 4;
            correctKeystrokes++;
            updateDisplayText();
        } else {
            errorCount++;
        }
    }

    private void startTimer() {
        if (timer != null) {
            timer.stop();
        }

        updateTimerLabel();

        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeLeft--;
            updateTimerLabel();
            if (timeLeft <= 0) {
                endTest();
            }
        }));
        timer.setCycleCount(timeLeft);
        timer.play();
    }

    private void updateTimerLabel() {
        timerLabel.setText(String.format("Time Left: %d seconds", timeLeft));
        timerLabel.setStyle(timeLeft <= 10 ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
    }

    private void endTest() {
        testActive = false;
        if (timer != null) {
            timer.stop();
        }

        typingArea.setEditable(false);

        double elapsedTimeMinutes = (System.currentTimeMillis() - startTime) / 60000.0;

        double wpm = Math.floor((correctKeystrokes / 5.0) / elapsedTimeMinutes);

        double accuracy = totalKeystrokes > 0 ? 
            ((double) correctKeystrokes / totalKeystrokes) * 100 : 0;

        displayText.getChildren().clear();
        Text resultText = new Text(String.format(
            "Test Complete!\nWPM: %.0f\nAccuracy: %.1f%%\n" +
            "Total Keystrokes: %d\nCorrect Keystrokes: %d\nErrors: %d",
            wpm, accuracy, totalKeystrokes, correctKeystrokes, errorCount));
        resultText.getStyleClass().add("result-text");
        displayText.getChildren().add(resultText);

        startButton.setText("Try Again");
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-menu.fxml"));
            Parent mainMenu = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(mainMenu));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}