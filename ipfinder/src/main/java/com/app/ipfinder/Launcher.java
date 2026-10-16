package com.app.ipfinder;

import javafx.application.Application;

/**
 * Entry point. Deliberately does NOT extend Application.
 * If the class with main() extends javafx.application.Application and the app
 * runs from the classpath (e.g. a fat JAR), the JVM can fail with
 * "JavaFX runtime components are missing".
 */
public class Launcher {

    public static void main(String[] args) {
        Application.launch(IpfinderApplication.class, args);
    }
}