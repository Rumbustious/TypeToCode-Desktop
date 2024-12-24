package rumb.typetocode;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainMenuController {

    @FXML
    private void handleTypingTestClick(ActionEvent event) {
        try {
            // Load TypingTestPage.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TypingTestPage.fxml"));
            Parent typingTestPage = loader.load();

            // Get the current stage from the event source
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(typingTestPage));
            stage.setTitle("Typing Test");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTutorialClick() {
        String url = "https://www.w3schools.com/java/";
        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSettingsClick(ActionEvent event) {
        try {
            // Load SettingsPage.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SettingsPage.fxml"));
            Parent settingsPage = loader.load();

            // Get the current stage from the event source
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(settingsPage));
            stage.setTitle("Settings");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}