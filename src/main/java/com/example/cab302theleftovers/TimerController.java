package com.example.cab302theleftovers;
import Time.*;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;



import java.awt.*;
import java.io.IOException;

public class TimerController {

    @FXML
    private Button BackBtn;
    @FXML
    private Button PauseBtn;
    @FXML
    private Button ResumeBtn;

    private Timer timer;

    public void initialize() {
        // Get offset and duration from main (Get offset and Get Duration)
        timer = new Timer(0, 1);
        timer.Control(Command.Start);
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
        else {
            // update errors at the bottom
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
        else {
            // update errors at the bottom
        }
    }


    @FXML
    protected void onBackButtonClick() throws IOException {
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
