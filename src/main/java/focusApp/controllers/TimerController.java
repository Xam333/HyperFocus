package focusApp.controllers;
import focusApp.models.Command;
import focusApp.models.Timer;

import focusApp.HelloApplication;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.Label;


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
        timer = new Timer(null, 0.5, this);
        timer.Control(Command.Start);

//        Timer T2 = new Timer(null, 10, this);
    }

    public void UpdateStopWatch(String TimeString){
        Platform.runLater(()->{
            StopWatch.setText(TimeString);
        });
    }

    public void UnlockPauseButton(){
        PauseBtn.setDisable(false);
    }


    @FXML
    protected void onPauseButtonClick(){
        // There should be no if statement here, either lock the button or hid it
        if (timer.isTimerRunning()){

            timer.Control(Command.Pause);
            HideButton(PauseBtn);
            ShowButton(ResumeBtn);
        }
    }

    @FXML
    protected void onResumeButtonClick(){
        // Check timer status
        if (timer.isTimerPaused()){

            timer.Control(Command.Resume);
            HideButton(ResumeBtn);
            ShowButton(PauseBtn);
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
