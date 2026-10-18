package com.app.ipfinder.ui.controller;

import com.app.ipfinder.ui.StageManager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.springframework.stereotype.Component;
import java.util.prefs.Preferences;

@Component
public class MainController {

    @FXML
    private StackPane contentPane;

    @FXML
    private Button lookupButton;

    @FXML
    private Button historyButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Button themeButton;

    private static final String DARK_MODE_KEY = "darkMode";

    private final Preferences preferences = Preferences.userNodeForPackage(MainController.class);

    private boolean darkMode;

    private final StageManager stageManager;

    public MainController(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    @FXML
    private void initialize() {
        showLookup();
        darkMode = preferences.getBoolean(DARK_MODE_KEY, false);

        Platform.runLater(() -> {
            applyTheme();
        });
    }

    @FXML
    private void showLookup() {
        stageManager.showLookup(contentPane);
        setActiveButton(lookupButton);
    }

    @FXML
    private void showHistory() {
        stageManager.showHistory(contentPane);
        setActiveButton(historyButton);
    }

    @FXML
    private void toggleTheme() {
        Scene scene = themeButton.getScene();

        if (scene == null) {
            return;
        }

        darkMode = !darkMode;

        preferences.putBoolean(DARK_MODE_KEY, darkMode);

        applyTheme();
    }

    private void applyTheme() {
        Scene scene = contentPane.getScene();

        if (scene == null) {
            return;
        }

        String theme = darkMode
                ? "/com/app/ipfinder/ui/dark.css"
                : "/com/app/ipfinder/ui/application.css";

        scene.getStylesheets().clear();
        scene.getStylesheets().add(
                getClass().getResource(theme).toExternalForm());

        themeButton.setText(
                darkMode ? "☀  Light Mode" : "☾  Dark Mode");
    }

    private void setActiveButton(Button activeButton) {
        lookupButton.getStyleClass().remove("active");
        historyButton.getStyleClass().remove("active");

        if (activeButton != null) {
            activeButton.getStyleClass().add("active");
        }
    }

    public void setStatus(String message) {
        statusLabel.setText(message);
    }
}