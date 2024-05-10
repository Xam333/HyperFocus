package focusApp.controllers;

import focusApp.HelloApplication;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    public Button startButton;
    public Pane blockedApplicationPane;
    public StackPane menuStackPane;
    public Button accountButton;
    public Button parentalControlsButton;
    public Button colourSettingsButton;
    public Button soundSettingsButton;

    @FXML
    private Label startTimeLabel;

    @FXML
    private Label endTimeLabel;

    @FXML
    private Slider startTimeSlider;

    @FXML
    private Slider endTimeSlider;

    public int startTime;
    public int endTime;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Listen for changes to the slider and update the label
        startTimeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int totalMinutes = newVal.intValue();  // Convert slider value to int
            this.startTime = totalMinutes;
            int hours = totalMinutes / 60;  // Get full hours
            int minutes = totalMinutes % 60;  // Get remaining minutes

            // Build the formatted time string
            String formattedTime;
            if (totalMinutes == 0) {
                formattedTime = "Now";
            }
            else if (hours > 0) {
                formattedTime = String.format("%d Hour%s %d Minute%s",
                        hours, hours > 1 ? "s" : "",
                        minutes, minutes > 1 ? "s" : "");
            } else {
                formattedTime = String.format("%d Minute%s", minutes, minutes > 1 ? "s" : "");
            }

            startTimeLabel.setText(formattedTime);  // Update the label with the formatted time
        });
        endTimeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int totalMinutes = newVal.intValue();  // Convert slider value to int
            this.endTime = totalMinutes;
            int hours = totalMinutes / 60;  // Get full hours
            int minutes = totalMinutes % 60;  // Get remaining minutes

            // Build the formatted time string
            String formattedTime;
            if (hours > 0) {
                formattedTime = String.format("%d Hour%s %d Minute%s",
                        hours, hours > 1 ? "s" : "",
                        minutes, minutes > 1 ? "s" : "");
            } else {
                formattedTime = String.format("%d Minute%s", minutes, minutes > 1 ? "s" : "");
            }

            endTimeLabel.setText(formattedTime);  // Update the label with the formatted time
        });
    }

    public void onBlockedApplicationsPaneClick(MouseEvent mouseEvent) throws IOException {
        Stage stage = (Stage) blockedApplicationPane.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/blocked-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        // Set scene stylesheet
        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
    }


    public void onStartButtonClick(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) startButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/timer-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        // Get the controller of the loaded scene
        TimerController timerController = fxmlLoader.getController();

        // Pass the start time and end time to the timer controller
        timerController.initialize(startTime, endTime);

        // Set scene stylesheet
        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
    }

    public void onMenuStackPaneEnter(MouseEvent actionEvent) {
        menuStackPane.setVisible(!menuStackPane.isVisible());
    }


    public void onMenuStackPaneExit(MouseEvent mouseEvent) {
        menuStackPane.setVisible(false);
    }

    public void onAccountButtonClick(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) accountButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/account-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
    }

    public void onParentalControlsButtonClick(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) parentalControlsButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
    }

    public void onColourSettingsButtonClick(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) colourSettingsButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
    }

    public void onSoundSettingsButtonClick(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) soundSettingsButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
    }

}
