package com.app.ipfinder.ui.controller;

import com.app.ipfinder.exception.InvalidIpException;
import com.app.ipfinder.exception.LookupFailedException;
import com.app.ipfinder.model.LookupEntry;
import com.app.ipfinder.service.IpLookupService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
public class LookupController {

    @FXML
    private TextField ipField;

    @FXML
    private Button lookupButton;

    @FXML
    private Label statusLabel;

    @FXML
    private VBox resultContainer;

    @FXML
    private Label ipLabel;

    @FXML
    private Label countryLabel;

    @FXML
    private Label cityLabel;

    @FXML
    private Label ispLabel;

    private final IpLookupService lookupService;
    private final ExecutorService backgroundExecutor;

    public LookupController(
            IpLookupService lookupService,
            ExecutorService backgroundExecutor
    ) {
        this.lookupService = lookupService;
        this.backgroundExecutor = backgroundExecutor;
    }

    @FXML
    private void initialize() {
        resultContainer.setVisible(false);
        resultContainer.setManaged(false);

        ipField.setOnAction(event -> lookup());
    }

    @FXML
    private void lookup() {
        String ip = ipField.getText();

        setLoading(true);
        hideResult();
        setStatus("");

        Task<LookupEntry> task = new Task<>() {

            @Override
            protected LookupEntry call() {
                return lookupService.lookup(ip);
            }
        };

        task.setOnSucceeded(event -> {
            LookupEntry entry = task.getValue();

            showResult(entry);
            setStatus("Lookup completed.");
            setLoading(false);
        });

        task.setOnFailed(event -> {
            Throwable exception = task.getException();

            if (exception instanceof InvalidIpException
                    || exception instanceof LookupFailedException) {

                setStatus(exception.getMessage());

            } else {
                setStatus("An unexpected error occurred.");
            }

            setLoading(false);
        });

        backgroundExecutor.submit(task);
    }

    private void showResult(LookupEntry entry) {
        ipLabel.setText(valueOrDash(entry.ip()));
        countryLabel.setText(valueOrDash(entry.country()));
        cityLabel.setText(valueOrDash(entry.city()));
        ispLabel.setText(valueOrDash(entry.isp()));

        resultContainer.setManaged(true);
        resultContainer.setVisible(true);
    }

    private void hideResult() {
        resultContainer.setManaged(false);
        resultContainer.setVisible(false);
    }

    private void setLoading(boolean loading) {
        lookupButton.setDisable(loading);
        ipField.setDisable(loading);

        lookupButton.setText(
                loading ? "Looking up..." : "Lookup"
        );
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank()
                ? "—"
                : value;
    }
}
