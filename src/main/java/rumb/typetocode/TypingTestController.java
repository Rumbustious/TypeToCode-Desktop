package rumb.typetocode;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypingTestController {
    @FXML private TextFlow displayText;
    @FXML private TextArea typingArea;
    @FXML private Label timerLabel;
    @FXML private Button startButton;

    private Timeline timer;
    private int timeLeft = 60;
    private String textToType;
    private int currentPosition = 0;
    private int errorCount = 0;
    private static final int MAX_ERRORS = 5;
    private boolean testActive = false;
    private int correctChars = 0;
    private int totalChars = 0;

    // Add these regex patterns as class fields
    private static final Pattern CODE_PATTERN = Pattern.compile(
        "\\b(public|class|static|void|main|String|System|out|println)\\b|" +  // keywords
        "(\"[^\"]*\")|" +                                                     // strings
        "([{};().])"                                                         // symbols
    );

    @FXML
    private void initialize() {
        typingArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (testActive) {
                handleTyping(oldValue, newValue);
            }
        });
    }

    @FXML
    private void handleStartButton(ActionEvent event) {
        testActive = true;
        currentPosition = 0;
        errorCount = 0;
        correctChars = 0;
        totalChars = 0;
        
        textToType = """
                public class HelloWorld {
                    public static void main(String[] args) {
                        System.out.println("Hello, World!");
                    }
                }""";
        
        typingArea.clear();
        typingArea.setEditable(true);
        updateDisplayText();
        startTimer();
    }

    private void handleTyping(String oldValue, String newValue) {
        if (!testActive) return;
    
        // Handle backspace
        if (newValue.length() < oldValue.length()) {
            currentPosition = Math.max(0, currentPosition - 1);
            errorCount = Math.max(0, errorCount - 1);
            updateDisplayText();
            return;
        }
    
        // Don't allow advancing until errors are fixed
        if (errorCount > 0) {
            // Only allow fixing the current position
            if (newValue.length() > oldValue.length()) {
                char expectedChar = textToType.charAt(currentPosition);
                char typedChar = newValue.charAt(currentPosition);
                
                if (typedChar == expectedChar) {
                    errorCount--;
                    if (errorCount == 0) {
                        typingArea.setStyle("-fx-text-fill: green;");
                    }
                } else {
                    typingArea.setText(oldValue);
                }
            }
            return;
        }
    
        // Check if current character is correct
        if (currentPosition < textToType.length() && 
            newValue.length() > oldValue.length()) {
            
            char expectedChar = textToType.charAt(currentPosition);
            char typedChar = newValue.charAt(newValue.length() - 1);
    
            // Handle tab character
            if (expectedChar == '\t' && typedChar == '\t') {
                currentPosition++;
                correctChars++;
                totalChars++;
                updateDisplayText();
                return;
            }
    
            if (typedChar != expectedChar) {
                errorCount++;
                typingArea.setStyle("-fx-text-fill: red;");
                totalChars++;
                return;
            }
    
            // Correct character typed
            currentPosition++;
            correctChars++;
            totalChars++;
            typingArea.setStyle("-fx-text-fill: green;");
            updateDisplayText();
    
            // Check if completed
            if (currentPosition >= textToType.length()) {
                endTest();
            }
        }
    }

    private void updateDisplayText() {
        displayText.getChildren().clear();
        
        // Split into segments
        String completedText = textToType.substring(0, currentPosition);
        String currentChar = currentPosition < textToType.length() ? 
                            String.valueOf(textToType.charAt(currentPosition)) : "";
        String remainingText = currentPosition < textToType.length() - 1 ? 
                              textToType.substring(currentPosition + 1) : "";

        // Add completed text with syntax highlighting and green background
        if (!completedText.isEmpty()) {
            formatCodeSegment(completedText, "-fx-background-color: rgba(76,175,80,0.2);"); // Light green background
        }

        // Add current character with syntax highlighting and cursor/error indication
        if (!currentChar.isEmpty()) {
            Text current = new Text(currentChar);
            applySyntaxHighlighting(current, currentChar);
            current.setStyle(current.getStyle() + ";" + (errorCount > 0 ? 
                "-fx-background-color: rgba(255,0,0,0.2); -fx-underline: true;" : // Light red background
                "-fx-background-color: rgba(33,150,243,0.2); -fx-underline: true;")); // Light blue background
            displayText.getChildren().add(current);
        }

        // Add remaining text with only syntax highlighting
        if (!remainingText.isEmpty()) {
            formatCodeSegment(remainingText, null);
        }
    }

    private void formatCodeSegment(String text, String additionalStyle) {
        Matcher matcher = CODE_PATTERN.matcher(text);
        int lastEnd = 0;
        
        while (matcher.find()) {
            // Add text before match
            if (matcher.start() > lastEnd) {
                Text normalText = new Text(text.substring(lastEnd, matcher.start()));
                normalText.setStyle("-fx-fill: #d4d4d4" + (additionalStyle != null ? ";" + additionalStyle : ""));
                displayText.getChildren().add(normalText);
            }

            // Add matched text with appropriate style
            String match = matcher.group();
            Text matchedText = new Text(match);
            
            if (matcher.group(1) != null) {
                matchedText.setStyle("-fx-fill: #569cd6" + (additionalStyle != null ? ";" + additionalStyle : "")); // keywords
            } else if (matcher.group(2) != null) {
                matchedText.setStyle("-fx-fill: #ce9178" + (additionalStyle != null ? ";" + additionalStyle : "")); // strings
            } else {
                matchedText.setStyle("-fx-fill: #d4d4d4" + (additionalStyle != null ? ";" + additionalStyle : "")); // symbols
            }
            
            displayText.getChildren().add(matchedText);
            lastEnd = matcher.end();
        }

        // Add remaining text
        if (lastEnd < text.length()) {
            Text remaining = new Text(text.substring(lastEnd));
            remaining.setStyle("-fx-fill: #d4d4d4" + (additionalStyle != null ? ";" + additionalStyle : ""));
            displayText.getChildren().add(remaining);
        }
    }

    private void applySyntaxHighlighting(Text text, String content) {
        Matcher matcher = CODE_PATTERN.matcher(content);
        if (matcher.matches()) {
            if (matcher.group(1) != null) {
                text.setStyle("-fx-fill: #569cd6"); // keywords
            } else if (matcher.group(2) != null) {
                text.setStyle("-fx-fill: #ce9178"); // strings
            } else {
                text.setStyle("-fx-fill: #d4d4d4"); // symbols
            }
        } else {
            text.setStyle("-fx-fill: #d4d4d4"); // normal text
        }
    }

    private void startTimer() {
        if (timer != null) {
            timer.stop();
        }
        
        timeLeft = 60;
        updateTimerLabel();
        
        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeLeft--;
            updateTimerLabel();
            if (timeLeft <= 0) {
                endTest();
            }
        }));
        timer.setCycleCount(60);
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
        
        double accuracy = (double) correctChars / totalChars * 100;
        double wpm = (correctChars / 5.0) / ((60 - timeLeft) / 60.0);
        
        displayText.getChildren().clear();
        Text resultText = new Text(String.format(
            "Test Complete!\nWPM: %.1f\nAccuracy: %.1f%%", 
            wpm, accuracy));
        resultText.setStyle("-fx-fill: green;");
        displayText.getChildren().add(resultText);
        
        startButton.setText("Try Again");
    }

    private void formatCodeExample(String code) {
        // Preserve indentation using actual tab characters
        StringBuilder formattedCode = new StringBuilder();
        String[] lines = code.split("\n");
        int indentLevel = 0;
        
        for (String line : lines) {
            String trimmedLine = line.trim();
            
            // Decrease indent for closing braces
            if (trimmedLine.startsWith("}")) {
                indentLevel = Math.max(0, indentLevel - 1);
            }
            
            // Add indentation
            for (int i = 0; i < indentLevel; i++) {
                formattedCode.append('\t');
            }
            
            formattedCode.append(trimmedLine).append('\n');
            
            // Increase indent after opening braces
            if (trimmedLine.endsWith("{")) {
                indentLevel++;
            }
        }
        
        textToType = formattedCode.toString();
    }
}