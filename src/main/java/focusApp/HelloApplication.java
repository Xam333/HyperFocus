package focusApp;

import focusApp.models.Timer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;


public class HelloApplication extends Application {

    // Constants defining the window title and size
    public static final String TITLE = "Focus App";
    public static final int WIDTH = 390;
    public static final int HEIGHT = 600;


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/hello-view.fxml"));
        String stylesheet = Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm();
        Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
        scene.getStylesheets().add(stylesheet);
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.setOnCloseRequest(windowEvent -> Timer.ForceStopTimer());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}