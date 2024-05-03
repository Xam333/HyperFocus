package focusApp.controllers;

import focusApp.models.Command;
import focusApp.models.Timer;
import focusApp.HelloApplication;

import focusApp.models.TimerState;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.shape.Arc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    private Button StopButton;
    @FXML
    private Button RestartButton;
    @FXML
    private Button ReturnButton;

    @FXML
    protected void onStopButtonClick(){
        timer.Control(Command.Stop);
        UpdateButtons();
    }
    @FXML
    protected void mouseInStopButton(){ StopButton.setContentDisplay(ContentDisplay.LEFT);}
    @FXML
    protected void mouseOutStopButton(){ StopButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);}


    @FXML
    protected void onPauseButtonClick(){
        timer.Control(Command.Pause);
        UpdateButtons();
    }
    @FXML
    protected void mouseInPauseButton(){ PauseButton.setContentDisplay(ContentDisplay.LEFT);}
    @FXML
    protected void mouseOutPauseButton(){ PauseButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);}

    @FXML
    protected void onResumeButtonClick(){
        timer.Control(Command.Resume);
        UpdateButtons();
    }
    @FXML
    protected void mouseInResumeButton(){ ResumeButton.setContentDisplay(ContentDisplay.LEFT);}
    @FXML
    protected void mouseOutResumeButton(){ PauseButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);}

    @FXML
    protected void onRestartButtonClick(){
        timer.Control(Command.Restart);
        UpdateButtons();
    }
    @FXML
    protected void mouseInRestartButton(){ RestartButton.setContentDisplay(ContentDisplay.LEFT);}
    @FXML
    protected void mouseOutRestartButton(){ RestartButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);}


    @FXML
    protected void onReturnButtonClick() throws IOException {
        Stage stage = (Stage) BackBtn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        // Set scene stylesheet
        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
    }
    @FXML
    protected void mouseInReturnButton(){ ReturnButton.setContentDisplay(ContentDisplay.LEFT);}
    @FXML
    protected void mouseOutReturnButton(){ ReturnButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);}


    public void UpdateButtons(){
        switch(timer.getTimerState()){
            case Running -> {
                StopButton.setDisable(false);
                PauseButton.setDisable(false);

                RestartButton.setDisable(true);
                ReturnButton.setDisable(true);
                ResumeButton.setDisable(true);

                Platform.runLater(() ->{
                    RestartButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    ReturnButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    ResumeButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                });
            }

            case Delayed,Restarting -> {
                StopButton.setDisable(false);

                RestartButton.setDisable(true);
                ReturnButton.setDisable(true);
                PauseButton.setDisable(true);
                ResumeButton.setDisable(true);

                Platform.runLater(() ->{
                    RestartButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    ReturnButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    PauseButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    ResumeButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                });

            }

            case Paused -> {
                StopButton.setDisable(false);
                ResumeButton.setDisable(false);

                RestartButton.setDisable(true);
                ReturnButton.setDisable(true);
                PauseButton.setDisable(true);

                Platform.runLater(() ->{
                    RestartButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    ReturnButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    PauseButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                });

            }

            case Stopped, Finished -> {
                RestartButton.setDisable(false);
                ReturnButton.setDisable(false);

                StopButton.setDisable(true);
                PauseButton.setDisable(true);
                ResumeButton.setDisable(true);

                Platform.runLater(() ->{
                    StopButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    PauseButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    ResumeButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                });

            }


        }
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
    public void UpdateArc(){
        Platform.runLater(() -> arc.setLength(((timer.getRunning_CD_MS() + 1) / timer.getRunning_TD_MS()) * 360));
    }
    public void HideArc(){
        arc.setLength(0.0);
        arc.setVisible(false);
    }
    public void ResetArc(){
        arc.setLength(360);
        arc.setVisible(true);
    }


    public void UpdateMiniArc(){
        Platform.runLater(() ->{
            if (timer.getTimerState() == TimerState.Delayed){
                MiniArc.setLength(((timer.getDelayed_CD_MS() + 1) / timer.getDelayed_TD_MS()) * 360);
            }else{
                MiniArc.setVisible(false);
            }
            // Timer can only be in a state of Delayed otherwise the Main timer is either running, paused or finished.
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

        // Set scene stylesheet
        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
    }

}