package com.example.cab302theleftovers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import Time.Timer;

public class HelloApplication extends Application {

    // Constants defining the window title and size
    public static final String TITLE = "Focus App";
    public static final int WIDTH = 390;
    public static final int HEIGHT = 644;


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.setOnCloseRequest(windowEvent -> Timer.ForceStopTimer());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}