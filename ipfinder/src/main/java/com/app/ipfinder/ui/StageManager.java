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

    private static final String FXML_BASE =
            "/com/app/ipfinder/ui/";

    private final ApplicationContext applicationContext;

    private Stage primaryStage;

    public StageManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;

        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void showMainWindow() {
        Parent root = load("/com/app/ipfinder/ui/main.fxml");

        Scene scene = new Scene(root, 1100, 700);

        primaryStage.setTitle("IP Finder");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showLookup(StackPane contentPane) {
        replaceContent(
                contentPane,
                "/com/app/ipfinder/ui/lookup.fxml"
        );
    }

    public void showHistory(StackPane contentPane) {
        replaceContent(
                contentPane,
                "/com/app/ipfinder/ui/history.fxml"
        );
    }

    private void replaceContent(
            StackPane contentPane,
            String resource
    ) {
        Parent view = load(resource);

        contentPane.getChildren().setAll(view);
    }

    private Parent load(String resource) {
        try {
            URL location = getClass()
                    .getResource(resource);

            if (location == null) {
                throw new IllegalStateException(
                        "FXML resource not found: " + resource
                );
            }

            FXMLLoader loader = new FXMLLoader(location);

            /*
             * Important:
             * Let Spring create the controller so constructor
             * injection works correctly.
             */
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
