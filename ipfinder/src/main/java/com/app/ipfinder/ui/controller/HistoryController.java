package com.app.ipfinder.ui.controller;

import com.app.ipfinder.model.LookupEntry;
import com.app.ipfinder.service.HistoryService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class HistoryController {

        @FXML
        private TableView<LookupEntry> historyTable;

        @FXML
        private TableColumn<LookupEntry, String> ipColumn;

        @FXML
        private TableColumn<LookupEntry, String> countryColumn;

        @FXML
        private TableColumn<LookupEntry, String> cityColumn;

        @FXML
        private TableColumn<LookupEntry, String> ispColumn;

        @FXML
        private TableColumn<LookupEntry, String> timestampColumn;

        @FXML
        private Button clearButton;

        private final HistoryService historyService;

        private final ObservableList<LookupEntry> history = FXCollections.observableArrayList();

        private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
                        .withZone(ZoneId.systemDefault());

        public HistoryController(HistoryService historyService) {
                this.historyService = historyService;
        }

        private String valueOrDash(String value) {
                return (value == null || value.isEmpty()) ? "—" : value;
        }

        @FXML
        private void initialize() {
                configureColumns();
                historyTable.setItems(history);

                refresh();
        }

        private void configureColumns() {

                ipColumn.setCellValueFactory(
                                cell -> new SimpleStringProperty(
                                                valueOrDash(cell.getValue().query())));

                countryColumn.setCellValueFactory(
                                cell -> new SimpleStringProperty(
                                                valueOrDash(cell.getValue().country())));

                cityColumn.setCellValueFactory(
                                cell -> new SimpleStringProperty(
                                                valueOrDash(cell.getValue().city())));

                ispColumn.setCellValueFactory(
                                cell -> new SimpleStringProperty(
                                                valueOrDash(cell.getValue().isp())));

                timestampColumn.setCellValueFactory(
                                cell -> new SimpleStringProperty(
                                                cell.getValue().timestamp() == null
                                                                ? "—"
                                                                : DATE_FORMATTER.format(
                                                                                cell.getValue().timestamp())));
        }

        public void refresh() {
                history.setAll(historyService.getAll());
        }

        @FXML
        private void clearHistory() {
                Alert confirmation = new Alert(
                                Alert.AlertType.CONFIRMATION);

                confirmation.setTitle("Clear History");
                confirmation.setHeaderText("Clear lookup history?");
                confirmation.setContentText(
                                "All saved lookup entries will be removed.");

                confirmation.showAndWait()
                                .filter(response -> response == ButtonType.OK)
                                .ifPresent(response -> {
                                        historyService.clear();
                                        refresh();
                                });
        }
}