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
    private TextField queryField;

    @FXML
    private Button lookupButton;

    @FXML
    private Label statusLabel;

    @FXML
    private VBox resultContainer;

    @FXML
    private Label queryLabel;

    @FXML
    private Label countryLabel;

    @FXML
    private Label countryCodeLabel;

    @FXML
    private Label regionLabel;

    @FXML
    private Label cityLabel;

    @FXML
    private Label zipLabel;

    @FXML
    private Label latLabel;

    @FXML
    private Label lonLabel;

    @FXML
    private Label timezoneLabel;

    @FXML
    private Label ispLabel;

    @FXML
    private Label orgLabel;

    @FXML
    private Label asLabel;

    private final IpLookupService lookupService;
    private final ExecutorService backgroundExecutor;

    public LookupController(
            IpLookupService lookupService,
            ExecutorService backgroundExecutor) {

        this.lookupService = lookupService;
        this.backgroundExecutor = backgroundExecutor;
    }

    @FXML
    private void initialize() {
        resultContainer.setVisible(false);
        resultContainer.setManaged(false);

        queryField.setOnAction(event -> lookup());
    }

    @FXML
    private void lookup() {

        String query = queryField.getText();

        setLoading(true);
        hideResult();
        setStatus("");

        Task<LookupEntry> task = new Task<>() {

            @Override
            protected LookupEntry call() {
                return lookupService.lookup(query);
            }
        };

        task.setOnSucceeded(event -> {

            LookupEntry entry = task.getValue();

            showResult(entry);

            setStatus("Lookup completed.");
            statusLabel.getStyleClass().add("success");

            setLoading(false);
        });

        task.setOnFailed(event -> {

            Throwable exception = task.getException();

            if (exception instanceof InvalidIpException
                    || exception instanceof LookupFailedException) {

                setStatus(exception.getMessage());
                statusLabel.getStyleClass().add("error");

            } else {

                exception.printStackTrace();

                setStatus("An unexpected error occurred.");
                statusLabel.getStyleClass().add("error");
            }

            setLoading(false);
        });

        backgroundExecutor.submit(task);
    }

    private void showResult(LookupEntry entry) {

        queryLabel.setText(
                valueOrDash(entry.query())
        );

        countryLabel.setText(
                valueOrDash(entry.country())
        );

        countryCodeLabel.setText(
                valueOrDash(entry.countryCode())
        );

        regionLabel.setText(
                valueOrDash(entry.regionName())
        );

        cityLabel.setText(
                valueOrDash(entry.city())
        );

        zipLabel.setText(
                valueOrDash(entry.zip())
        );

        latLabel.setText(
                valueOrDash(entry.lat())
        );

        lonLabel.setText(
                valueOrDash(entry.lon())
        );

        timezoneLabel.setText(
                valueOrDash(entry.timezone())
        );

        ispLabel.setText(
                valueOrDash(entry.isp())
        );

        orgLabel.setText(
                valueOrDash(entry.org())
        );

        asLabel.setText(
                valueOrDash(entry.asInfo())
        );

        resultContainer.setManaged(true);
        resultContainer.setVisible(true);
    }

    private void hideResult() {
        resultContainer.setManaged(false);
        resultContainer.setVisible(false);
    }

    private void setLoading(boolean loading) {

        lookupButton.setDisable(loading);
        queryField.setDisable(loading);

        lookupButton.setText(
                loading ? "Looking up..." : "Lookup"
        );
    }

    private void setStatus(String message) {

        statusLabel.getStyleClass()
                .removeAll("success", "error");

        statusLabel.setText(message);
    }

    private String valueOrDash(String value) {

        return value == null || value.isBlank()
                ? "—"
                : value;
    }

    private String valueOrDash(Double value) {

        return value == null
                ? "—"
                : String.valueOf(value);
    }
}
