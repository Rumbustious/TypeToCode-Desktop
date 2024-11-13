package rumb.typetocode;

import javafx.fxml.FXML;
import javafx.scene.control.Label;



public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();
        welcomeText.setText("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
    }
}