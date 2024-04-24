package com.example.cab302theleftovers;
import Time.*;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.Label;



import java.awt.*;
import java.io.IOException;

public class TimerController {
    private Timer timer;
    @FXML
    private Button BackBtn;
    @FXML
    private Button PauseBtn;
    @FXML
    private Button ResumeBtn;
    @FXML
    private Label StopWatch;


    public void initialize() {
        // Get offset and duration from main (Get offset and Get Duration)
        timer = new Timer(0, 1, this);
        timer.Control(Command.Start);
    }

    public void UpdateStopWatch(String TimeString){
        Platform.runLater(()->{
            StopWatch.setText(TimeString);
        });
    }


    @FXML
    protected void onPauseButtonClick(){
        // Check timer status
        if (timer.isTimerRunning()){
            timer.Control(Command.Pause);

            PauseBtn.setVisible(false);
            PauseBtn.setPrefWidth(0);
            PauseBtn.setManaged(false);

            ResumeBtn.setVisible(true);
            ResumeBtn.setPrefWidth(100);
            ResumeBtn.setManaged(true);
        }
    }

    @FXML
    protected void onResumeButtonClick(){
        // Check timer status
        if (timer.isTimerPaused()){
            timer.Control(Command.Resume);

            PauseBtn.setVisible(true);
            PauseBtn.setPrefWidth(100);
            PauseBtn.setManaged(true);

            ResumeBtn.setVisible(false);
            ResumeBtn.setPrefWidth(0);
            ResumeBtn.setManaged(false);
        }
    }

    @FXML
    protected void onBackButtonClick() throws IOException {
        timer.Control(Command.Stop); // kills the timer so no duplicates are running
        Stage stage = (Stage) BackBtn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        stage.setScene(scene);
    }

    @FXML
    protected void onStopButtonClick() throws IOException {
        timer.Control(Command.Stop);
        Stage stage = (Stage) BackBtn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        stage.setScene(scene);
    }

}
