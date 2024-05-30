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

public class TimerController {

    private Timer timer;
    private boolean InParentalControl;

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

    /**
     * Handles the onToggleListenClick action by turning the text-to-speech
     * function off or on depending on its previous state
     */
    @FXML
    protected void onToggleListenClick() {
        timer.TurnOnTTS(ToggleListen.isSelected());
    }

    /**
     * Handles the onStopButtonClick action by stopping the timer
     */
    @FXML
    protected void onStopButtonClick(){
        // Add code to check Passwords here. if in parental control mode.
        timer.Control(Command.Stop);
    }

    /**
     * Handles mouse hover event over stop button by highlighting the
     * button when mouse enters the field
     */
    @FXML
    protected void mouseInStopButton(){ StopButton.setContentDisplay(ContentDisplay.LEFT); }

    /**
     * Handles mouse hover event over stop button by un-highlighting the
     * button when mouse exits the field
     */
    @FXML
    protected void mouseOutStopButton(){ StopButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);}

    /**
     * Handles the onPauseButtonClick action by pausing the timer
     */
    @FXML
    protected void onPauseButtonClick(){
        timer.Control(Command.Pause);
    }

    /**
     * Handles mouse hover event over pause button by highlighting the
     * button when mouse enters the field
     */
    @FXML
    protected void mouseInPauseButton(){ PauseButton.setContentDisplay(ContentDisplay.LEFT);}

    /**
     * Handles mouse hover event over pause button by un-highlighting the
     * button when mouse exits the field
     */
    @FXML
    protected void mouseOutPauseButton(){ PauseButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);}

    /**
     * Handles the onResumeButtonClick action by resuming the timer
     */
    @FXML
    protected void onResumeButtonClick(){
        timer.Control(Command.Resume);
    }

    /**
     * Handles mouse hover event over resume button by highlighting the
     * button when mouse enters the field
     */
    @FXML
    protected void mouseInResumeButton(){ ResumeButton.setContentDisplay(ContentDisplay.LEFT);}

    /**
     * Handles mouse hover event over resume button by un-highlighting the
     * button when mouse exits the field
     */
    @FXML
    protected void mouseOutResumeButton(){ ResumeButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);}

    /**
     * Handles the onRestartButtonClick action by restarting the timer
     */
    @FXML
    protected void onRestartButtonClick(){
        timer.Control(Command.Restart);
    }

    /**
     * Handles mouse hover event over restart button by highlighting the
     * button when mouse enters the field
     */
    @FXML
    protected void mouseInRestartButton(){ RestartButton.setContentDisplay(ContentDisplay.LEFT);}

    /**
     * Handles mouse hover event over restart button by un-highlighting the
     * button when mouse exits the field
     */
    @FXML
    protected void mouseOutRestartButton(){ RestartButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);}

    /**
     * Handles the onReturnButtonClick action by loading the
     * main-view FXML and the appropriate stylesheet
     * @throws IOException
     *      If an exception occurred while loading the FXML file
     */
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

    /**
     * Handles mouse hover event over return button by highlighting the
     * button when mouse enters the field
     */
    @FXML
    protected void mouseInReturnButton(){ ReturnButton.setContentDisplay(ContentDisplay.LEFT);}

    /**
     * Handles mouse hover event over return button by un-highlighting the
     * button when mouse exits the field
     */
    @FXML
    protected void mouseOutReturnButton(){ ReturnButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);}

    /**
     * States button identifiers used for disabled, enabled, graphic display states
     */
    private enum ButtonStates{
        Disable, Enable, DisplayGraphic
    }

    /**
     *
     * @param Buttons
     *      An array of the buttons to update
     * @param States
     *      An array of the states for each button
     */
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

    /**
     * Manages the states of the control buttons on the timer.
     * If in parental mode, button updates are bypassed until the timer
     * is finished
     */
    public void ButtonStateManager(){
        Button[] AllButtons = new Button[]{PauseButton, ResumeButton, StopButton, RestartButton, ReturnButton};

        // Bypass Button updates while in  parent control mode until the timer state is "finished".
        if (InParentalControl && timer.getTimerState() != TimerState.Finished){
            return;
        }

        UpdateButtonStates(AllButtons);
    }

    /**
     * Updates the states of the buttons provided based on the
     * current timer state
     * @param AllButtons
     *      An array for all buttons being updated
     */
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

    /**
     * Gets the running state configuration for the buttons provided
     * @return
     *      An array representing the state of each button
     */
    private ButtonStates[] getRunningState(){
        return new ButtonStates[]{
                ButtonStates.Enable,    // Pause
                ButtonStates.Disable,   // Resume
                ButtonStates.Enable,    // Stop
                ButtonStates.Disable,   // Restart
                ButtonStates.Disable    // Return
        };
    }

    /**
     * Gets the pre-run state configuration for the buttons provided
     * @return
     *      An array representing the state of each button
     */
    private ButtonStates[] getPreRunState(){
        return new ButtonStates[]{
                ButtonStates.Disable,   // Pause
                ButtonStates.Disable,   // Resume
                ButtonStates.Enable,    // Stop
                ButtonStates.Disable,   // Restart
                ButtonStates.Disable    // Return
        };
    }

    /**
     * Gets the paused state configuration for the buttons provided
     * @return
     *      An array representing the state of each button
     */
    private ButtonStates[] getPausedState(){
        return new ButtonStates[]{
                ButtonStates.Disable,   // Pause
                ButtonStates.Enable,    // Resume
                ButtonStates.Disable,   // Stop
                ButtonStates.Disable,   // Restart
                ButtonStates.Disable    // Return
        };
    }

    /**
     * Gets the end state configuration for the buttons provided
     * @return
     *      An array representing the state of each button
     */
    private ButtonStates[] getEndState(){
        return new ButtonStates[]{
                ButtonStates.Disable,   // Pause
                ButtonStates.Disable,   // Resume
                ButtonStates.Disable,   // Stop
                ButtonStates.Enable,    // Restart
                ButtonStates.Enable     // Return
        };
    }

    /**
     * Initialises the timer with the given start time, end time, and alarm
     *
     * @param startTime
     *      The start time for the timer
     * @param endTime
     *      The end time for the timer
     * @param Alarm
     *      The alarm the timer will use
     */
    public void initialize(double startTime, double endTime, Notification Alarm) {
        timer = new Timer(startTime, endTime, this, Alarm);
        timer.Control(Command.Start);
        // Get the bool of parent mode here.
        InParentalControl = false;
        ButtonStateManager();
    }

    /**
     * Updates the timer status based on the status
     */
    public void UpdateTimerStatus(){
        Platform.runLater(() -> TimerStatus.setText(timer.getStatus()));
    }

    /**
     * Updates the text of the stop watch with the formatted current timer value
     */
    public void UpdateStopWatch(){
        Platform.runLater(() -> StopWatch.setText(timer.FormatTime()));
    }

    /**
     * Updates the length of the arc based on current timer value
     */
    public void UpdateArc(){
        Platform.runLater(() -> arc.setLength(((timer.getRCDinMS() + 1) / timer.getRTDinMS()) * 360));
    }

    /**
     * Updates the length of the mini arc based on current timer value
     */
    public void UpdateMiniArc(){
        Platform.runLater(() ->  MiniArc.setLength(((timer.getDCDinMS() + 1) / timer.getDTDinMS()) * 360));
    }

    /**
     * Updates the attributes of a specific arc element
     *
     * @param ThisArc
     *      The arc being updated
     * @param Length
     *      The length of the arc
     * @param Visibility
     *      Used to track the visibility of the arc, if null the
     *      visibility is no changed
     */
    private void UpdateArcAttributes(Arc ThisArc, int Length, Boolean Visibility){
        ThisArc.setLength(Length);
        if (Visibility != null){
            ThisArc.setVisible(Visibility);
        }
    }

    /**
     * Updates the graphical user interface elements
     * based on the timer state
     */
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