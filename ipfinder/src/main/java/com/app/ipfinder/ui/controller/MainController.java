package com.app.ipfinder.ui.controller;

import com.app.ipfinder.ui.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.springframework.stereotype.Component;

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

    private final StageManager stageManager;

    public MainController(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    @FXML
    private void initialize() {
        showLookup();
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