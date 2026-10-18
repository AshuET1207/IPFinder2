package com.app.ipfinder.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
public class StageManager {

    private final ApplicationContext applicationContext;

    private Stage primaryStage;

    public StageManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;

        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(500);
    }

    public void showMainWindow() {

        Parent root = load(
                "/com/app/ipfinder/ui/main.fxml"
        );

        Scene scene = new Scene(
                root,
                1000,
                600
        );

        primaryStage.setTitle("IP Finder");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showLookup(StackPane contentPane) {

        contentPane.getChildren().setAll(
                load("/com/app/ipfinder/ui/lookup.fxml")
        );
    }

    public void showHistory(StackPane contentPane) {

        contentPane.getChildren().setAll(
                load("/com/app/ipfinder/ui/history.fxml")
        );
    }

    private Parent load(String resource) {

        try {

            URL location =
                    getClass().getResource(resource);

            if (location == null) {
                throw new IllegalStateException(
                        "FXML resource not found: " + resource
                );
            }

            FXMLLoader loader =
                    new FXMLLoader(location);

            loader.setControllerFactory(
                    applicationContext::getBean
            );

            return loader.load();

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Could not load FXML: " + resource,
                    e
            );
        }
    }
}