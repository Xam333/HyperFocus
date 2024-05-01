package focusApp.controllers;

import focusApp.models.Command;
import focusApp.models.Timer;

import focusApp.HelloApplication;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.shape.Arc;
import java.io.IOException;

public class TimerController {

    private Timer timer;

    @FXML
    private Button BackBtn;

    @FXML
    private Label StopWatch;
    @FXML
    private Label TimerStatus;

    @FXML
    private Arc arc;



    @FXML
    private Group PauseButton;
    @FXML
    private Group ResumeButton;


    @FXML
    protected void onPauseButtonClick(){
        timer.Control(Command.Pause);
        PauseButton.setVisible(false);
        ResumeButton.setVisible(true);
    }
    @FXML
    protected void onResumeButtonClick(){
        timer.Control(Command.Resume);
        PauseButton.setVisible(true);
        ResumeButton.setVisible(false);
    }


    public void initialize() {
        // Get offset and duration from main (Get offset and Get Duration)
        timer = new Timer(null, 1.25, this);
        timer.Control(Command.Start);
    }

    public void UpdateStopWatch(String TimeString){
        Platform.runLater(()-> {

            if (timer.isTimerFinished()){
                arc.setVisible(false);
            } else {
                StopWatch.setText(TimeString);
                arc.setLength(((timer.getRunning_CD_MS() + 1) / timer.getRunning_TD_MS()) * 360);
            }
        });

    }

    @FXML
    private Arc MiniArc;
    public void UpdateMiniTimer(){
        Platform.runLater(() -> {

            if(timer.isTimerRunning()){
                MiniArc.setVisible(false);
            } else{
                MiniArc.setLength(((timer.getDelayed_CD_MS() + 1) / timer.getDelayed_TD_MS()) * 360);

            }
        });
    }


    public void UpdateTimerStatusLabel(String Status){
        Platform.runLater(() -> TimerStatus.setText(Status));
    }


    @FXML
    protected void onBackButtonClick() throws IOException {
        timer.Control(Command.Stop); // kills the timer so no duplicates are running
        Stage stage = (Stage) BackBtn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        stage.setScene(scene);
    }



}
