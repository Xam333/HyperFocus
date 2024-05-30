package focusApp.controllers;

import focusApp.models.timer.Command;
import focusApp.models.timer.Notification;
import focusApp.models.timer.Timer;
import focusApp.HelloApplication;

import focusApp.models.colour.UserConfig;
import focusApp.models.timer.TimerState;
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

public class  TimerController {

    private Timer timer;
    private boolean InParentalControl;
    private boolean PasswordGood;


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
    protected void onStopButtonClick() throws IOException {
        // Add code to check Passwords here. if in parental control mode.
        timer.Control(Command.Stop);
    }
    @FXML
    protected void mouseInStopButton(){ StopButton.setContentDisplay(ContentDisplay.LEFT); }
    @FXML
    protected void mouseOutStopButton(){ StopButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);}


    @FXML
    protected void onPauseButtonClick() throws IOException {
        timer.Control(Command.Pause);
    }
    @FXML
    protected void mouseInPauseButton(){ PauseButton.setContentDisplay(ContentDisplay.LEFT);}
    @FXML
    protected void mouseOutPauseButton(){ PauseButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);}


    @FXML
    protected void onResumeButtonClick() throws IOException {
        timer.Control(Command.Resume);
    }
    @FXML
    protected void mouseInResumeButton(){ ResumeButton.setContentDisplay(ContentDisplay.LEFT);}
    @FXML
    protected void mouseOutResumeButton(){ ResumeButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);}


    @FXML
    protected void onRestartButtonClick() throws IOException {
        timer.Control(Command.Restart);
    }
    @FXML
    protected void mouseInRestartButton(){ RestartButton.setContentDisplay(ContentDisplay.LEFT);}
    @FXML
    protected void mouseOutRestartButton(){ RestartButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);}


    @FXML
    protected void onReturnButtonClick() throws IOException {
        BlockedController.resetBlockedList();
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


    private enum ButtonStates{
        Disable, Enable, DisplayGraphic
    }
    private void UpdateButtons(Button[] Buttons, ButtonStates[] States){
        if (Buttons.length == States.length){
            for (int i = 0; i < Buttons.length; i++){
                switch (States[i]){
                    case Enable -> Buttons[i].setDisable(false);
                    case Disable -> Buttons[i].setDisable(true);
                    case DisplayGraphic -> Buttons[i].setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                }
            }
        } else {
            System.out.println("Button State mapping failed: Error length is not equal.");
        }

    }
    public void ButtonStateManager(){
        Button[] AllButtons = new Button[]{PauseButton, ResumeButton, StopButton, RestartButton, ReturnButton};

        // Bypass Button updates while in  parent control mode until the timer state is "finished".
        if (InParentalControl && timer.getTimerState() != TimerState.Finished){
            return;
        }

        UpdateButtonStates(AllButtons);
    }

    private void UpdateButtonStates(Button[] AllButtons){
        ButtonStates[] States;
        switch(timer.getTimerState()) {

            case Running -> States = getRunningState();
            case Delayed, Restarting -> States = getPreRunState();
            case Paused -> States = getPausedState();
            case Stopped, Finished -> States = getEndState();
            default -> States = new ButtonStates[0];
        }
        UpdateButtons(AllButtons, States);

        Platform.runLater(() -> {
            ButtonStates[] LimpStates = new ButtonStates[]{
                    ButtonStates.DisplayGraphic,    // Pause
                    ButtonStates.DisplayGraphic,    // Resume
                    ButtonStates.DisplayGraphic,    // Stop
                    ButtonStates.DisplayGraphic,    // Restart
                    ButtonStates.DisplayGraphic     // Return
            };
            UpdateButtons(AllButtons, LimpStates);
        });
    }

    private ButtonStates[] getRunningState(){
        return new ButtonStates[]{
                ButtonStates.Enable,    // Pause
                ButtonStates.Disable,   // Resume
                ButtonStates.Enable,    // Stop
                ButtonStates.Disable,   // Restart
                ButtonStates.Disable    // Return
        };
    }
    private ButtonStates[] getPreRunState(){
        return new ButtonStates[]{
                ButtonStates.Disable,   // Pause
                ButtonStates.Disable,   // Resume
                ButtonStates.Enable,    // Stop
                ButtonStates.Disable,   // Restart
                ButtonStates.Disable    // Return
        };
    }
    private ButtonStates[] getPausedState(){
        return new ButtonStates[]{
                ButtonStates.Disable,   // Pause
                ButtonStates.Enable,    // Resume
                ButtonStates.Disable,   // Stop
                ButtonStates.Disable,   // Restart
                ButtonStates.Disable    // Return
        };
    }
    private ButtonStates[] getEndState(){
        return new ButtonStates[]{
                ButtonStates.Disable,   // Pause
                ButtonStates.Disable,   // Resume
                ButtonStates.Disable,   // Stop
                ButtonStates.Enable,    // Restart
                ButtonStates.Enable     // Return
        };
    }

    public void initialize(double startTime, double endTime, Notification Alarm) throws IOException {
        timer = new Timer(startTime, endTime, this, Alarm);
        timer.Control(Command.Start);
        // Get the bool of parent mode here.
        InParentalControl = false;
        ButtonStateManager();
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

    private void UpdateArcAttributes(Arc ThisArc, int Length, Boolean Visibility){
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