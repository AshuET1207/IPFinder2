package com.app.ipfinder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.URL;

@SpringBootApplication
public class IpfinderApplication extends Application {

    private ConfigurableApplicationContext springContext;

    /** Runs on the JavaFX launcher thread, before start(). Boot Spring here. */
    @Override
    public void init() {
        String[] args = getParameters().getRaw().toArray(new String[0]);
        springContext = new SpringApplicationBuilder(IpfinderApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    /** Runs on the JavaFX Application Thread. Build the window here. */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/main-view.fxml"));
        loader.setControllerFactory(springContext::getBean);

        Parent root = loader.load();
        Scene scene = new Scene(root, 900, 600);

        URL css = getClass().getResource("/css/styles.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        stage.setTitle("IPFinder");
        stage.setScene(scene);
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        stage.show();
    }

    /** Called when the last window closes. Shut Spring down cleanly. */
    @Override
    public void stop() {
        if (springContext != null) {
            springContext.close();
        }
    }
}