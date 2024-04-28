package focusApp.controllers;
import focusApp.models.Command;
import focusApp.models.Timer;

import focusApp.HelloApplication;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.shape.Arc;


import java.io.IOException;

public class TimerController {
    public enum ButtonControl {
        Show, Hide, Lock, Unlock
    }
    private Timer timer;

    @FXML
    private Button BackBtn;
    @FXML
    private Button PauseBtn;
    @FXML
    private Button ResumeBtn;
    @FXML
    private Button StopBtn;

    @FXML
    private Label StopWatch;
    @FXML
    private Label TimerStatus;

    @FXML
    private Arc TimeArc;
    @FXML
    private StackPane StackPane;



    public void initialize() {
        // Get offset and duration from main (Get offset and Get Duration)
        timer = new Timer(null, 0.5, this);
        timer.Control(Command.Start);
            }

    public void UpdateStopWatch(String TimeString){
        Platform.runLater(()-> {
            StopWatch.setText(TimeString);
            TimeArc.setLength((timer.getCountingSeconds() / timer.getTotalSeconds()) * 360);
        });
    }

    public void UpdateTimerStatusLabel(String Status){
        Platform.runLater(() -> TimerStatus.setText(Status));
    }

    public void UnlockPauseButton(){
        UpdateButton(PauseBtn, ButtonControl.Unlock);
    }

    @FXML
    protected void onPauseButtonClick(){
        // There should be no if statement here, either lock the button or hid it
        if (timer.isTimerRunning()){

            timer.Control(Command.Pause);
            UpdateButton(PauseBtn, ButtonControl.Hide);
            UpdateButton(ResumeBtn, ButtonControl.Show);
        }
    }

    @FXML
    protected void onResumeButtonClick(){
        // Check timer status
        if (timer.isTimerPaused()){

            timer.Control(Command.Resume);
            UpdateButton(ResumeBtn, ButtonControl.Hide);
            UpdateButton(PauseBtn, ButtonControl.Show);
        }
    }


    public void UpdateButton(Button btnToUpdate, ButtonControl Command){
        switch(Command){
            case Hide -> HideButton(btnToUpdate);
            case Show -> ShowButton(btnToUpdate);
            case Lock -> LockButton(btnToUpdate);
            case Unlock -> UnlockButton(btnToUpdate);
        }
    }
    private void ShowButton(Button btnToShow){
        btnToShow.setVisible(true);
        btnToShow.setPrefWidth(100);
        btnToShow.setManaged(true);
    }
    private void HideButton(Button btnToShow){
        btnToShow.setVisible(false);
        btnToShow.setPrefWidth(0);
        btnToShow.setManaged(false);
    }
    private void LockButton(Button btnToLock){
        btnToLock.setDisable(true);
    }
    private void UnlockButton(Button btnToUnlock){
        btnToUnlock.setDisable(false);
    }


    @FXML
    protected void onBackButtonClick() throws IOException {
        timer.Control(Command.Stop); // kills the timer so no duplicates are running
        Stage stage = (Stage) BackBtn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        stage.setScene(scene);
    }

    @FXML
    protected void onStopButtonClick() throws IOException {
        timer.Control(Command.Stop);
        Stage stage = (Stage) BackBtn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        stage.setScene(scene);
    }

}
