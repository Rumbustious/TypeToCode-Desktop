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

            // Set the new scene
            stage.setScene(new Scene(typingTestPage));
            stage.setTitle("Typing Test");
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
