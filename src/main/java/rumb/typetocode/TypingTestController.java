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
        
        int pos = 0;
        String[] lines = textToType.split("\n", -1);
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            
            // Handle indentation
            int indentCount = 0;
            while (indentCount < line.length() && line.charAt(indentCount) == '\t') {
                Text tabText = new Text("    "); // 4 spaces for each tab
                tabText.setStyle("-fx-fill: #666666;"); // Subtle color for indentation
                displayText.getChildren().add(tabText);
                indentCount++;
            }
            
            // Format the rest of the line
            String codeLine = line.substring(indentCount);
            formatCodeLine(codeLine, pos < currentPosition ? codeLine.length() : 
                          currentPosition - pos);
            
            pos += line.length() + 1; // +1 for newline
            
            // Add newline except for last line
            if (i < lines.length - 1) {
                displayText.getChildren().add(new Text("\n"));
            }
        }
    }
    private void formatCodeLine(String line, int cursorPos) {
    // Add syntax highlighting for keywords, strings, etc.
    Pattern pattern = Pattern.compile(
        "\\b(public|class|static|void|main|String)\\b|" +
        "(\"[^\"]*\")|" +
        "([{};()])"
    );
    
    Matcher matcher = pattern.matcher(line);
    int lastEnd = 0;
    
    while (matcher.find()) {
        // Add text before the match
        if (matcher.start() > lastEnd) {
            addTextSegment(line.substring(lastEnd, matcher.start()), "normal");
        }

        // Add the matched text with appropriate style
        String match = matcher.group();
        if (matcher.group(1) != null) {
            addTextSegment(match, "keyword");
        } else if (matcher.group(2) != null) {
            addTextSegment(match, "string");
        } else {
            addTextSegment(match, "normal");
        }
        
        lastEnd = matcher.end();
    }
    
    // Add remaining text
    if (lastEnd < line.length()) {
        addTextSegment(line.substring(lastEnd), "normal");
    }
    }

    private void addTextSegment(String text, String style) {
        Text textNode = new Text(text);
        switch (style) {
            case "keyword" -> textNode.setStyle("-fx-fill: #569cd6;");
            case "string" -> textNode.setStyle("-fx-fill: #ce9178;");
            default -> textNode.setStyle("-fx-fill: #d4d4d4;");
        }
        displayText.getChildren().add(textNode);
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