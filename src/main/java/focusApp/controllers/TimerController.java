package focusApp.controllers;

import focusApp.models.timer.Command;
import focusApp.models.timer.Notification;
import focusApp.models.timer.Timer;
import focusApp.HelloApplication;

import focusApp.models.colour.UserConfig;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.shape.Arc;

import java.io.IOException;
import java.util.Objects;

public class TimerController {

    private Timer timer;


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
    private ToggleButton ToggleListen;

    @FXML
    protected void onToggleListenClick() {
        timer.TurnOnTTS(ToggleListen.isSelected());
    }


    @FXML
    protected void onStopButtonClick(){ timer.Control(Command.Stop); }
    @FXML
    protected void mouseInStopButton(){ StopButton.setContentDisplay(ContentDisplay.LEFT); }
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
        Stage stage = (Stage) ReturnButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);

        // Set scene stylesheet
        if (UserConfig.FindCSSFile()){
            scene.getStylesheets().add(UserConfig.getCSSFilePath().toUri().toString());
        } else {
            scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("stylesheet.css")).toExternalForm());
        }
        stage.setScene(scene);
    }
    @FXML
    protected void mouseInReturnButton(){ ReturnButton.setContentDisplay(ContentDisplay.LEFT);}
    @FXML
    protected void mouseOutReturnButton(){ ReturnButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);}


    private void UpdateButtons(Button[] Buttons, Boolean Attribute){
        for(Button B : Buttons){
            if (Attribute != null){
                B.setDisable(Attribute);
            } else {
                B.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }

        }
    }
    public void ButtonStateManager(){
        switch(timer.getTimerState()){
            case Running -> {
                UpdateButtons(new Button[]{StopButton, PauseButton}, false);
                UpdateButtons(new Button[]{RestartButton, ReturnButton, ResumeButton}, true);

                Platform.runLater(() ->{
                    UpdateButtons(new Button[]{RestartButton, ReturnButton, ResumeButton}, null);
                });
            }

            case Delayed, Restarting -> {
                UpdateButtons(new Button[]{StopButton}, false);
                UpdateButtons(new Button[]{RestartButton, ReturnButton, PauseButton,ResumeButton}, true);

                Platform.runLater(() ->{
                    UpdateButtons(new Button[]{RestartButton, ReturnButton, PauseButton, ResumeButton}, null);
                });
            }

            case Paused -> {
                UpdateButtons(new Button[]{ResumeButton}, false);
                UpdateButtons(new Button[]{StopButton, RestartButton, ReturnButton, PauseButton}, true);

                Platform.runLater(() ->{
                    UpdateButtons(new Button[]{RestartButton, ReturnButton, PauseButton}, null);
                });
            }

            case Stopped, Finished -> {
                UpdateButtons(new Button[]{RestartButton, ReturnButton}, false);
                UpdateButtons(new Button[]{StopButton, PauseButton, ResumeButton}, true);

                Platform.runLater(() ->{
                    UpdateButtons(new Button[]{StopButton, PauseButton, ResumeButton}, null);
                });
            }
        }
    }

    public void initialize(double startTime, double endTime, Notification Alarm) {
        timer = new Timer(startTime, endTime, this, Alarm);
        timer.Control(Command.Start);
    }

    public void UpdateTimerStatus(){
        Platform.runLater(() -> TimerStatus.setText(timer.getStatus()));
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

    private void UpdateArcAttributes(Arc ThisArc,int Length, Boolean Visibility){
        ThisArc.setLength(Length);
        if (Visibility != null){
            ThisArc.setVisible(Visibility);
        }
    }
    public void UpdateGUI(){
        Platform.runLater(() -> {
            switch (timer.getTimerState()){
                case Delayed, Restarting-> {
                    DelayedGroup.setVisible(true);
                    UpdateArcAttributes(MiniArc, 360, true);
                    UpdateArcAttributes(arc, 360, true);
                    StopWatch.setVisible(false);
                }

                case Running, Stopped -> {
                    DelayedGroup.setVisible(false);
                    UpdateArcAttributes(MiniArc, 0, false);
                    UpdateArcAttributes(arc, 360, null);
                    StopWatch.setVisible(true);
                }

                case Finished -> UpdateArcAttributes(arc, 0, false);

            }
        });
    }

}