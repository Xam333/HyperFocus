package focusApp;
import java.io.IOException;
import java.util.Objects;

import focusApp.models.Timer;
import fr.brouillard.oss.cssfx.CSSFX;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import fr.brouillard.oss.cssfx.CSSFX;


public class HelloApplication extends Application {

    // Constants defining the window title and size
    public static final String TITLE = "Hyper Focus";
    public static final int WIDTH = 390;
    public static final int HEIGHT = 600;


    @Override
    public void start(Stage stage) throws IOException {
        // Load fxml file
        FXMLLoader root = new FXMLLoader(HelloApplication.class.getResource("fxml/home-view.fxml"));
        // Set scene
        Scene scene = new Scene(root.load(), WIDTH, HEIGHT);
        // Set scene stylesheet
        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        // Set application icon
        stage.getIcons().add(new Image(Objects.requireNonNull(HelloApplication.class.getResourceAsStream("images/logoSmall.png"))));

        // Add scene to stage
        stage.setScene(scene);

        // Set window properties
        stage.setTitle(TITLE);      // Set window title
        stage.setResizable(false);  // User cannot resize window
        stage.setX(1130);           // Set X position of window
        stage.setY(280);            // Set Y position of window

        // Show stage
        stage.setOnCloseRequest(windowEvent -> Timer.ForceStopTimer());
        stage.show();
        CSSFX.start();
    }

    public static void main(String[] args) {
        launch();
    }
}