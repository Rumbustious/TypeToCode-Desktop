package rumb.typetocode;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.prefs.Preferences;

public class SettingsController {
    @FXML private RadioButton oneMinute;
    @FXML private RadioButton threeMinutes;
    @FXML private RadioButton fiveMinutes;

    private final ToggleGroup timeGroup = new ToggleGroup();
    private final Preferences prefs = Preferences.userRoot().node("typetocode");

    @FXML
    private void initialize() {
        oneMinute.setToggleGroup(timeGroup);
        threeMinutes.setToggleGroup(timeGroup);
        fiveMinutes.setToggleGroup(timeGroup);

        // Load saved preference
        int savedDuration = prefs.getInt("testDuration", 60);
        switch(savedDuration) {
            case 180 -> threeMinutes.setSelected(true);
            case 300 -> fiveMinutes.setSelected(true);
            default -> oneMinute.setSelected(true);
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        int duration = 60;
        if (threeMinutes.isSelected()) duration = 180;
        if (fiveMinutes.isSelected()) duration = 300;
        prefs.putInt("testDuration", duration);
        handleBack(event);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            // Load main-menu.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-menu.fxml"));
            Parent mainMenu = loader.load();

            // Get the current stage from the event source
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(mainMenu));
            stage.setTitle("Main Menu");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
