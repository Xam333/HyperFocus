package focusApp.controllers;

import focusApp.models.Command;
import focusApp.models.State;
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
    private Button PauseButton;
    @FXML
    private Button ResumeButton;


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

        Double TO = 0.05;
        Double TD =  0.05;
        timer = new Timer(TO, TD, this);
        timer.Control(Command.Start);


    }


    public void UpdateTimerStatus(){ Platform.runLater(() -> TimerStatus.setText(timer.getStatus())); }
    public void UpdateStopWatch(){ Platform.runLater(() -> StopWatch.setText(timer.FormatTime())); }
    private void UpdateArc(){Platform.runLater(() -> arc.setLength(((timer.getRunning_CD_MS() + 1) / timer.getRunning_TD_MS()) * 360));}

    public void UpdatePaReButtons(boolean Lock) {
        Platform.runLater(() -> {
            PauseButton.setDisable(Lock);
            ResumeButton.setDisable(Lock);
        });
    }

    public void DelayedStopWatch(){
        Platform.runLater(()-> {
            MiniArc.setLength(((timer.getDelayed_CD_MS() + 1) / timer.getDelayed_TD_MS()) * 360);

            UpdateStopWatch();
            UpdateTimerStatus();
        });
    }
    public void RunningStopWatch(){
        Platform.runLater(()-> {
            MiniArc.setVisible(false);


//            arc.setLength(((timer.getRunning_CD_MS() + 1) / timer.getRunning_TD_MS()) * 360);
            UpdateArc();
            UpdateStopWatch();
            UpdateTimerStatus();
        });
    }
    public void FinishedStopWatch(){
        Platform.runLater(()-> {
            arc.setVisible(false);

            UpdateStopWatch();
            UpdateTimerStatus();
        });

    }



    @FXML
    private Arc MiniArc;


    @FXML
    protected void onBackButtonClick() throws IOException {
        timer.Control(Command.Stop); // kills the timer so no duplicates are running
        Stage stage = (Stage) BackBtn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        stage.setScene(scene);
    }



}
