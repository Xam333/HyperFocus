package Time;

import Notifications.Notification;
import com.example.cab302theleftovers.TimerController;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class creates a timer and the methods control it.
 */
public class Timer {
    /**
     * Used for tracking the state of the timer.
     */
    private enum State {
        Running, NotRunning, Paused, PreRun
    }

    private State Timer_State;
    public boolean isTimerPaused() { return Timer_State == State.Paused; }
    public  boolean isTimerPreRun() { return Timer_State == State.PreRun; }
    public boolean isTimerRunning() { return Timer_State == State.Running; }
    public boolean isTimerNotRunning() { return Timer_State == State.NotRunning; }


    private LocalTime Timer_End;
    private final TimerController Timer_Controller;

    /**
     * Stores the value of the timer offset.
     */
    private final double Timer_Preset_Offset;

    /**
     * Stores the value of the timer duration.
     */
    private final double Timer_Preset_Duration;


    /**
     * Gets the timer offset to add to a preset.
     * @return Timer offset.
     */
    public double getTimer_Preset_Offset() { return Timer_Preset_Offset; }

    /**
     * Gets the timer duration to add to a preset.
     * @return Timer duration.
     */
    public double getTimer_Preset_Duration() { return Timer_Preset_Duration; }

    /**
     * Global scheduler for the timer.
     */
    private static ScheduledExecutorService TimeScheduler;
    private Duration PausedDuration;
    private Duration CountingDuration;


    /**
     * The format of time as 12 hour time(hh:mm:ss a).
     */
    private final DateTimeFormatter Timer_12_Format = DateTimeFormatter.ofPattern("h:mm:ss a");

    /**
     * Basic constructor for the timer class.
     * @param Timer_Controller The calling time controller.
     * @param TimerOffset   The offset of minutes before the timer starts.
     * @param TimerDuration The duration of the timer in minutes (0 to 1439).
     */
    public Timer (Double TimerOffset, Double TimerDuration, TimerController Timer_Controller){
        Timer_Preset_Offset = TimerOffset == null ? 0.0 : (TimerOffset * 60);
        Timer_Preset_Duration = TimerDuration == null ? 0.0 : (TimerDuration * 60);
        Timer_State = State.NotRunning;

        System.out.println("Timer_Preset_Offset: " + Timer_Preset_Offset + "sec");
        System.out.println("Timer_Preset_Duration: " + Timer_Preset_Duration + "sec");

        LocalTime timer_Start = LocalTime.now().plusSeconds((long) Timer_Preset_Offset);
        this.Timer_End = timer_Start.plusSeconds((long)Timer_Preset_Duration);
        this.Timer_Controller = Timer_Controller;

        boolean TimeCheck = Timer_End.isBefore(timer_Start);
        CountingDuration = Duration.between(timer_Start, Timer_End).plusHours(TimeCheck ? 24 : 0);

        System.out.println("Timer Start: " + timer_Start.format(Timer_12_Format));
        System.out.println("Timer End: " + Timer_End.format(Timer_12_Format));
        System.out.println("Counting Duration: " + CountingDuration);
    }

    /**
     * This method is used to execute commands during the timer.
     */
    private void RunningTimer(){
        // Do the blocking stuff here.

        // Update GUI stuff here.
        Timer_Controller.UpdateStopWatch(FormatTime());
        CountingDuration = CountingDuration.minusSeconds(1);

        // Check if the timer has ended.
        if(CountingDuration.getSeconds() < 0 && Timer_State == State.Running){
            Control(Command.Stop);

            // Play alert sound here.
            Notification.PlaySound(-10);
            // Stop blocking stuff here, and return to the main page(GUI).

        }
    }

    /**
     * Formats the timer as (hh:mm:ss).
     * @return Formatted time (hh:mm:ss)
     */
    private String FormatTime(){
        long H = CountingDuration.toHours();
        long M = CountingDuration.minusHours(H).toMinutes();
        long S = CountingDuration.minusHours(H).minusMinutes(M).toSeconds();

        return String.format("%02d:%02d:%02d", H, M, S);
    }

    /**
     * Enforces a state upon the timer.
     * @param EnforceThis The state that the timer will enforce.
     */
    private void EnforceState(State EnforceThis){
        Timer_State = EnforceThis;
    }

    /**
     * Controls the timer.
     * @param command The command to run.
     */
    public void Control(Command command){
        switch (command) {
            case Start -> {if (Timer_Preset_Offset != 0){ PreStart();} else { Start(); }}
            case Stop -> Stop();
            case Pause -> Pause();
            case Resume -> Resume();
            default -> throw new IllegalArgumentException("Command: " + command + " is invalid.");
        }
    }

    /**
     * Begins the Pre-start period of the timer.
     */
    private void PreStart(){
        // Debug.
        System.out.println("Timer PreRun at: " + LocalTime.now().format(Timer_12_Format));
        // End Debug.

        EnforceState(State.PreRun);
        Timer_Controller.UpdateStopWatch(FormatTime());
        TimeScheduler = Executors.newScheduledThreadPool(1);
        TimeScheduler.scheduleAtFixedRate(this::Start, (long)Timer_Preset_Offset, 1, TimeUnit.SECONDS);
    }


    /**
     * Starts the timer.
     */
    private void Start(){
        // Debug.
        System.out.println("Timer Started at: " + LocalTime.now().format(Timer_12_Format));
        // End Debug.

        if (Timer_Preset_Offset != 0){ TimeScheduler.shutdownNow(); }

        EnforceState(State.Running);

        Timer_Controller.UnlockPauseButton();
        TimeScheduler = Executors.newScheduledThreadPool(1);
        TimeScheduler.scheduleAtFixedRate(this::RunningTimer, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Stops the timer.
     */
    private void Stop(){
        // Debug.
        System.out.println("Timer Stopped at: " + LocalTime.now().format(Timer_12_Format));
        // End Debug.

        EnforceState(State.NotRunning);
        // Get total time here.
        TimeScheduler.shutdownNow();
    }

    /**
     * Resumes the timer.
     */
    private void Resume(){
        // Debug.
        System.out.println("Timer Resumed at: " + LocalTime.now().format(Timer_12_Format));
        // End Debug.

        EnforceState(State.Running);
        Timer_End = LocalTime.now().plusSeconds(PausedDuration.toSeconds());
        CountingDuration = PausedDuration;
        TimeScheduler = Executors.newScheduledThreadPool(1);
        TimeScheduler.scheduleAtFixedRate(this::RunningTimer, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Pauses the timer.
     */
    private void Pause(){
        // Debug.
        System.out.println("Timer Paused at: " + LocalTime.now().format(Timer_12_Format));
        // End Debug.
        EnforceState(State.Paused);
        PausedDuration = Duration.between(LocalTime.now(), Timer_End);
        TimeScheduler.shutdownNow();
    }

    /**
     * Forces the timer to stop if the application is closed.
     */
    public static void ForceStopTimer(){
        if(TimeScheduler != null && !TimeScheduler.isShutdown()){
            TimeScheduler.shutdown();
            try{
                if(!TimeScheduler.awaitTermination(5, TimeUnit.SECONDS)){
                    TimeScheduler.shutdownNow();
                }
            } catch (InterruptedException E){
                TimeScheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
