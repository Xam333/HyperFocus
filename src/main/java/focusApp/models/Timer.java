package focusApp.models;

import focusApp.controllers.TimerController;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class creates a timer and the methods to control it.
 */
public class Timer {

    private final boolean DebugMode = false;


    public TimerState getTimerState() {
        return Timer_State;
    }

    private static TimerState Timer_State;



    // Main information needed for the timer.
    private LocalTime Timer_End;
    private LocalTime Timer_Start;
    private final TimerController Timer_Controller;

    /**
     * Stores the value of the timer offset and the timer duration.
     */
    private final double Timer_Preset_Offset;
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
    private Duration Paused_C_Duration;


    private Duration Delayed_C_Duration;
    private Duration Delayed_T_Duration;

    public double getDelayed_CD_MS(){
        return Delayed_C_Duration.toMillis();
    }
    public double getDelayed_TD_MS(){
        return Delayed_T_Duration.toMillis();
    }


    private Duration Running_C_Duration;
    private Duration Running_T_Duration;

    public double getRunning_CD_MS(){
        return Running_C_Duration.toMillis();
    }
    public double getRunning_TD_MS(){
        return Running_T_Duration.toMillis();
    }


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
        this.Timer_Controller = Timer_Controller;

        CreateTimer();

        if (DebugMode){
            System.out.println("Timer_Preset_Offset: " + Timer_Preset_Offset + "sec");
            System.out.println("Timer_Preset_Duration: " + Timer_Preset_Duration + "sec");
            System.out.println("Timer Start: " + Timer_Start.format(Timer_12_Format));
            System.out.println("Timer End: " + Timer_End.format(Timer_12_Format));
            System.out.println("Counting Duration: " + FormatTime());
            System.out.println("Timer TimerState: " + Timer_State);
        }
    }


    private void CreateTimer(){
        Timer_State = (Timer_Preset_Offset != 0.0) ? TimerState.Delayed : TimerState.Running;

        this.Timer_Start = LocalTime.now().plusSeconds((long) Timer_Preset_Offset);
        this.Timer_End = Timer_Start.plusSeconds((long)Timer_Preset_Duration);

        boolean TimeCheck = Timer_End.isBefore(Timer_Start);
        Running_C_Duration = Duration.between(Timer_Start, Timer_End).plusHours(TimeCheck ? 24 : 0);
        Running_T_Duration = Running_C_Duration;

        if(Timer_State == TimerState.Delayed){
            Delayed_C_Duration = Duration.between(LocalTime.now(),Timer_Start).plusHours(TimeCheck ? 24 : 0);
            Delayed_T_Duration = Delayed_C_Duration;
        }
    }

    /**
     * This method is used to execute commands during the timer.
     */


    private void DelayedTime(){
        Delayed_C_Duration = Delayed_C_Duration.minusMillis(1);

        if(Delayed_C_Duration.getSeconds() < 0) Start();

        Timer_Controller.UpdateMiniArc();
    }

    private void RunningTime(){
        Running_C_Duration = Running_C_Duration.minusMillis(1);

        if(Running_C_Duration.getSeconds() < 0) Finish();

        Timer_Controller.UpdateArc();
        Timer_Controller.UpdateStopWatch();
    }

    /**
     * Formats the timer as (hh:mm:ss).
     * @return Formatted time (hh:mm:ss)
     */
    public String FormatTime(){
        Duration T = Running_C_Duration;

        long Hours = T.toHours();
        long Minutes = T.minusHours(Hours).toMinutes();
        long Seconds = T.minusHours(Hours).minusMinutes(Minutes).toSeconds();

        switch (Hours >= 1 ? 0 : Minutes >= 1 ? 1 : Seconds >= 0 ? 2 : -1){
            // Case 0: If Hours is >= 1, returns a time format like this (hh:mm:ss)
            case 0 -> { return String.format("%02d:%02d:%02d", Hours, Minutes, Seconds ); }

            // Case 1: if Minutes is >= 1, returns a time format like this (mm:ss)
            case 1 -> { return String.format("%02d:%02d", Minutes, Seconds); }

            // Case 2: if Seconds is >= 0, returns a time format like this (ss)
            case 2 -> { return String.format("%02d", Seconds); }

            default -> { return "--:--"; }
        }
    }

    /**
     * Enforces a state upon the timer.
     * @param EnforceThis The state that the timer will enforce.
     */
    private void EnforceState(TimerState EnforceThis){
        if (DebugMode){
            System.out.println("Updating State: " + Timer_State + " --> " + EnforceThis);
        }
        Timer_State = EnforceThis;
    }
    public String getStatus(){
        switch (Timer_State){
            case Running -> { return "Timer is Running."; }
            case Paused -> { return "Timer is Paused."; }
            case Finished -> { return "Timer is Finished"; }
            case Delayed -> { return "Timer Starting at: " + Timer_Start.format(Timer_12_Format); }
            case Stopped -> { return "Timer Stopped."; }
            default -> throw new IllegalArgumentException("Invalid Time TimerState: " + Timer_State);
        }
    }
    /**
     * Controls the timer.
     * @param command The command to run.
     */
    public void Control(Command command){
        switch (command) {
            case Start -> { if(Timer_State == TimerState.Delayed) DelayedStart(); else Start(); }
            case Stop -> Stop();
            case Finish -> Finish();
            case Pause -> Pause();
            case Resume -> Resume();
            case Restart -> Restart();
            default -> throw new IllegalArgumentException("Command: " + command + " is invalid.");
        }
    }

    private void DelayedStart(){
        // State is enforced in the constructor.

        // Update GUI elements to their Pre-State for the current state.
        Timer_Controller.UpdateTimerStatus();
        Timer_Controller.UpdateStopWatch();
        Timer_Controller.UpdateButtons();

        // Start the Scheduler.
        TimeScheduler = Executors.newScheduledThreadPool(1);
        TimeScheduler.scheduleAtFixedRate(this::DelayedTime, 0, 1, TimeUnit.MILLISECONDS);
    }

    /**
     * Starts the timer.
     */
    private void Start(){
        // If timer is in a state of Delayed shutdown the existing scheduler and enforce Running state,
        // otherwise state is enforced in the constructor.
        if(TimeScheduler != null && !TimeScheduler.isShutdown() && Timer_State == TimerState.Delayed){
            EnforceState(TimerState.Running);
            TimeScheduler.shutdownNow();
        }

        // Update GUI elements to their Pre-State for the current state.
        Timer_Controller.UpdateMiniArc();
        Timer_Controller.UpdateButtons();
        Timer_Controller.UpdateTimerStatus();

        // Start the Scheduler.
        TimeScheduler = Executors.newScheduledThreadPool(1);
        TimeScheduler.scheduleAtFixedRate(this::RunningTime, 0, 1, TimeUnit.MILLISECONDS);
    }

    /**
     * Stops the timer.
     */
    private void Finish(){
        // If timer is in a state of Running shutdown the existing scheduler and enforce Finished state.
        if(TimeScheduler != null && !TimeScheduler.isShutdown() && Timer_State == TimerState.Running){
            EnforceState(TimerState.Finished);
            TimeScheduler.shutdownNow();
        }

        // Update GUI elements to their Pre-State for the current state.
        Timer_Controller.HideArc();
        Timer_Controller.UpdateStopWatch();
        Timer_Controller.UpdateTimerStatus();
        Timer_Controller.UpdateButtons();
        Notification.PlaySound(-10);

        // Timer is finished.
    }

    private void Stop(){

        if(TimeScheduler != null && !TimeScheduler.isShutdown() && (Timer_State == TimerState.Running || Timer_State == TimerState.Delayed)){
            EnforceState(TimerState.Stopped);
            TimeScheduler.shutdownNow();
        }
        // Drain the Running Counting Duration to -1.
        Running_C_Duration = Duration.ZERO.minusSeconds(1);

        // Update GUI elements to their Pre-State for the current state.
        Timer_Controller.UpdateTimerStatus();
        Timer_Controller.UpdateStopWatch();
        Timer_Controller.HideArc();
        Timer_Controller.UpdateButtons();
    }

    private void Restart(){
        // Time scheduler has been shutdown either through the timer finishing or the timer being stopped early.
        EnforceState(TimerState.Restarting);

        Timer_Controller.ResetArc();
        Timer_Controller.UpdateButtons();

        CreateTimer();
        Control(Command.Start);
    }

    /**
     * Resumes the timer.
     */
    private void Resume(){
        // State Enforcement and Pre-State tasks.
        Timer_End = LocalTime.now().plus(Paused_C_Duration.toMillis(), ChronoUnit.MILLIS);
        Running_C_Duration = Paused_C_Duration;
        EnforceState(TimerState.Running);

        // Update GUI elements to their Pre-State for the current state.
        Timer_Controller.UpdateTimerStatus();
        Timer_Controller.UpdateStopWatch();
        Timer_Controller.UpdateButtons();

        // Start the Scheduler.
        TimeScheduler = Executors.newScheduledThreadPool(1);
        TimeScheduler.scheduleAtFixedRate(this::RunningTime, 0, 1, TimeUnit.MILLISECONDS);
    }

    /**
     * Pauses the timer.
     */
    private void Pause(){
        // State Enforcement and Pre-State tasks.
        if(TimeScheduler != null && !TimeScheduler.isShutdown() && Timer_State == TimerState.Running){
            Paused_C_Duration = Duration.between(LocalTime.now(), Timer_End);
            EnforceState(TimerState.Paused);
            TimeScheduler.shutdownNow();
        }
        // Update GUI elements to their Pre-State for the current state.
        Timer_Controller.UpdateTimerStatus();
        Timer_Controller.UpdateStopWatch();
        Timer_Controller.UpdateButtons();
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
