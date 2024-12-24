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
    private int errorPosition = -1;
    private int validTextLength = 0;
    private int errorCount = 0;
    private static final int MAX_ERRORS = 10;
    private boolean testActive = false;
    private int correctChars = 0;
    private int totalChars = 0;
    private String wrongText = ""; // Add this field to track wrong characters
    private long startTime;

    // Add these regex patterns as class fields
    private static final Pattern CODE_PATTERN = Pattern.compile(
        "\\b(public|class|static|void|main|String|System|out|println)\\b|" +  // keywords
        "(\"[^\"]*\")|" +                                                     // strings
        "([{};().])"                                                         // symbols
    );

    private static final Pattern JAVA_KEYWORDS = Pattern.compile(
        "\\b(public|class|static|void|main|String|System|out|println|package|import|" +
        "private|protected|final|abstract|interface|extends|implements|return|new|this|" +
        "if|else|for|while|do|break|continue|switch|case|default|try|catch|finally|throw|throws)\\b"
    );
    
    private static final int TAB_WIDTH = 4;

    @FXML
    private void initialize() {
        // Prevent tab from changing focus
        typingArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                event.consume();
                handleTabPress();
            }
        });

        // Disable default tab behavior in TextArea
        typingArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.TAB) {
                event.consume();
            }
        });

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
        startTime = System.currentTimeMillis();
        
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
            if (!wrongText.isEmpty()) {
                // Remove last character from wrong text
                wrongText = wrongText.substring(0, wrongText.length() - 1);
                // Update typing area style based on wrong text status
                if (wrongText.isEmpty()) {
                    typingArea.setStyle("-fx-text-fill: green;");
                }
            } else {
                currentPosition--;
                validTextLength--;
            }
            updateDisplayText();
            return;
        }

        // Check error limit
        if (errorCount >= MAX_ERRORS) {
            typingArea.setText(oldValue);
            return;
        }

        // Handle new character
        if (currentPosition < textToType.length() && newValue.length() > oldValue.length()) {
            char expectedChar = textToType.charAt(currentPosition);
            char typedChar = newValue.charAt(newValue.length() - 1);

            // If we have wrong text, accumulate it
            if (!wrongText.isEmpty()) {
                wrongText += typedChar;
                typingArea.setStyle("-fx-text-fill: red;");
                if (wrongText.length() >= MAX_ERRORS) {
                    typingArea.setText(oldValue);
                }
                updateDisplayText();
                return;
            }

            // Check if new character is correct
            if (typedChar != expectedChar) {
                wrongText += typedChar;
                typingArea.setStyle("-fx-text-fill: red;");
                errorCount++;
                totalChars++;
                updateDisplayText();
                return;
            }

            // Correct character typed
            currentPosition++;
            validTextLength++;
            correctChars++;
            totalChars++;
            typingArea.setStyle("-fx-text-fill: green;");

            updateDisplayText();

            if (currentPosition >= textToType.length()) {
                endTest();
            }
        }
    }

    private void updateDisplayText() {
        displayText.getChildren().clear();
        
        // Split into completed and remaining text
        String completed = textToType.substring(0, currentPosition);
        String current = currentPosition < textToType.length() ? 
                        String.valueOf(textToType.charAt(currentPosition)) : "";
        String remaining = currentPosition < textToType.length() - 1 ? 
                          textToType.substring(currentPosition + 1) : "";

        // Add completed text
        if (!completed.isEmpty()) {
            Text completedText = new Text(completed);
            completedText.getStyleClass().addAll("typing-text", "completed-text");
            displayText.getChildren().add(completedText);
        }

        // Add current character with cursor
        if (!current.isEmpty()) {
            Text cursorText = new Text(current);
            cursorText.getStyleClass().addAll(
                "typing-text",
                "cursor",
                wrongText.isEmpty() ? "cursor-active" : "cursor-error"
            );
            displayText.getChildren().add(cursorText);
        }

        // Add remaining text
        if (!remaining.isEmpty()) {
            Text remainingText = new Text(remaining);
            remainingText.getStyleClass().add("typing-text");
            displayText.getChildren().add(remainingText);
        }
    }

    private void formatCodeBlock(String code) {
        String[] lines = code.split("\n");
        int pos = 0;

        for (String line : lines) {
            // Handle indentation
            int indent = getIndentLevel(line);
            if (indent > 0) {
                Text indentText = new Text(" ".repeat(indent * 4));
                indentText.getStyleClass().add("code-text");
                if (pos < currentPosition) {
                    indentText.getStyleClass().add("typed-text");
                }
                displayText.getChildren().add(indentText);
            }
            
            // Format code content
            formatCodeLine(line.trim(), pos + indent * 4);
            
            if (pos < code.length()) {
                displayText.getChildren().add(new Text("\n"));
            }
            pos += line.length() + 1;
        }
        
        // Add cursor
        if (currentPosition < code.length()) {
            Text cursor = new Text(String.valueOf(code.charAt(currentPosition)));
            cursor.getStyleClass().addAll("cursor", wrongText.isEmpty() ? "cursor-active" : "cursor-error");
            cursor.setTranslateY(-1); // Slight adjustment for better cursor visibility
        }
    }

    private void formatCodeLine(String line, int startPos) {
        Matcher matcher = JAVA_KEYWORDS.matcher(line);
        int lastEnd = 0;

        while (matcher.find()) {
            // Add non-keyword text
            if (matcher.start() > lastEnd) {
                addTextSegment(line.substring(lastEnd, matcher.start()), 
                             startPos + lastEnd, "code-text");
            }
            
            // Add keyword
            addTextSegment(matcher.group(), startPos + matcher.start(), "keyword");
            lastEnd = matcher.end();
        }

        // Add remaining text
        if (lastEnd < line.length()) {
            addTextSegment(line.substring(lastEnd), startPos + lastEnd, "code-text");
        }
    }

    private void addTextSegment(String text, int position, String styleClass) {
        Text textNode = new Text(text);
        textNode.getStyleClass().addAll(styleClass);
        
        if (position < currentPosition) {
            textNode.getStyleClass().add("completed-text");
        } else if (wrongText.isEmpty() && position == currentPosition) {
            textNode.getStyleClass().add("cursor");
        }
        
        displayText.getChildren().add(textNode);
    }

    private int getIndentLevel(String line) {
        int spaces = 0;
        while (spaces < line.length() && line.charAt(spaces) == ' ') {
            spaces++;
        }
        return spaces / TAB_WIDTH;
    }

    private boolean isTabExpectedAtCurrentPosition() {
        // Check if we're at the start of a line that requires indentation
        String textBeforeCursor = textToType.substring(0, currentPosition);
        int lastNewline = textBeforeCursor.lastIndexOf('\n');
        if (lastNewline == -1) return false;
        
        String currentLine = textBeforeCursor.substring(lastNewline + 1);
        return currentLine.isEmpty() && shouldLineBeIndented(currentPosition);
    }

    private boolean shouldLineBeIndented(int position) {
        // Check previous line for opening brace
        String[] lines = textToType.substring(0, position).split("\n");
        if (lines.length <= 1) return false;
        return lines[lines.length - 2].trim().endsWith("{");
    }

    private void handleTabPress() {
        if (!testActive || !wrongText.isEmpty()) return;

        // Check for expected indentation
        int expectedSpaces = 0;
        int pos = currentPosition;
        
        while (pos < textToType.length() && textToType.charAt(pos) == ' ' && expectedSpaces < 4) {
            expectedSpaces++;
            pos++;
        }

        if (expectedSpaces == 4) {
            // Tab is correct here - advance 4 spaces
            for (int i = 0; i < 4; i++) {
                currentPosition++;
                correctChars++;
            }
            totalChars++;
            typingArea.setStyle("-fx-text-fill: green;");
        } else {
            // Wrong tab usage - count as one error
            wrongText = " ";
            errorCount++;
            totalChars++;
            typingArea.setStyle("-fx-text-fill: red;");
        }
        
        updateDisplayText();
    }

    private boolean isValidTabPosition() {
        if (wrongText.isEmpty() && currentPosition < textToType.length()) {
            String currentLine = getCurrentLine();
            return currentLine.matches("^\\s*$"); // Line contains only whitespace
        }
        return false;
    }

    private String getCurrentLine() {
        String textBeforeCursor = textToType.substring(0, currentPosition);
        int lastNewline = textBeforeCursor.lastIndexOf('\n');
        return lastNewline == -1 ? textBeforeCursor : 
               textBeforeCursor.substring(lastNewline + 1);
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
        
        long endTime = System.currentTimeMillis();
        double elapsedTimeMinutes = (endTime - startTime) / 60000.0;
        
        // Calculate WPM: (total keys pressed / 5) / time in minutes
        double wpm = Math.floor((totalChars / 5.0) / elapsedTimeMinutes);
        
        // Calculate accuracy: (correct keys / total keys) * 100
        double accuracy = totalChars > 0 ? ((double) correctChars / totalChars) * 100 : 0;
        
        displayText.getChildren().clear();
        Text resultText = new Text(String.format(
            "Test Complete!\nWPM: %.0f\nAccuracy: %.1f%%", 
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