package focusApp.controllers;

import focusApp.models.Command;
import focusApp.models.Notification;
import focusApp.models.Timer;
import focusApp.HelloApplication;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.shape.Arc;

import java.io.IOException;
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
    private Arc MiniArc;
    @FXML
    private Group DelayedGroup;


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

    }
    @FXML
    protected void mouseInStopButton(){
        StopButton.setContentDisplay(ContentDisplay.LEFT);
    }
    @FXML
    protected void mouseOutStopButton(){ StopButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);}


    @FXML
    protected void onPauseButtonClick(){
        timer.Control(Command.Pause);
    }
    @FXML
    protected void mouseInPauseButton(){ PauseButton.setContentDisplay(ContentDisplay.LEFT);}
    @FXML
    protected void mouseOutPauseButton(){ PauseButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);}


    @FXML
    protected void onResumeButtonClick(){
        timer.Control(Command.Resume);
    }
    @FXML
    protected void mouseInResumeButton(){ ResumeButton.setContentDisplay(ContentDisplay.LEFT);}
    @FXML
    protected void mouseOutResumeButton(){ ResumeButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);}


    @FXML
    protected void onRestartButtonClick(){
        timer.Control(Command.Restart);
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

            case Delayed, Restarting -> {
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
                ResumeButton.setDisable(false);

                StopButton.setDisable(true);
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
        Double TD =  120.0;
        timer = new Timer(TO, TD, this);

        timer.Control(Command.Start);
    }

    public void UpdateTimerStatus(){
        Platform.runLater(() ->{
            TimerStatus.setText(timer.getStatus());
            Notification.SpeakText(timer.getStatus());
        });
    }
    public void UpdateStopWatch(){
        Platform.runLater(() -> StopWatch.setText(timer.FormatTime()));
    }
    public void UpdateArc(){
        Platform.runLater(() -> arc.setLength(((timer.getRCDinMS() + 1) / timer.getRTDinMS()) * 360));
    }
    public void UpdateMiniArc(){
        Platform.runLater(() ->  MiniArc.setLength(((timer.getDCDinMS() + 1) / timer.getDTDinMS()) * 360));
    }

    public void UpdateGUI(){
        Platform.runLater(() -> {
            switch (timer.getTimerState()){
                case Delayed, Restarting-> {
                    DelayedGroup.setVisible(true);
                    MiniArc.setLength(360);
                    MiniArc.setVisible(true);
                    arc.setLength(360);
                    arc.setVisible(true);
                    StopWatch.setVisible(false);
                }
                case Running, Stopped -> {
                    DelayedGroup.setVisible(false);
                    MiniArc.setLength(0);
                    MiniArc.setVisible(false);
                    arc.setLength(360);
                    StopWatch.setVisible(true);
                }
                case Finished -> {
                    arc.setLength(0);
                    arc.setVisible(false);
                }
            }
        });
    }

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