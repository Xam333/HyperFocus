package focusApp.models;

import focusApp.controllers.TimerController;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class creates a timer and the methods to control it.
 */
public class Timer {

    private final boolean DebugMode = false;


    private State Timer_State;
    public State getTimerState(){ return Timer_State; }


    // Main information needed for the timer.
    private LocalTime Timer_End;
    private final LocalTime Timer_Start;
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
    private final Duration Running_T_Duration;

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
        Timer_State = (Timer_Preset_Offset != 0.0) ? State.Delayed : State.Running;

        this.Timer_Start = LocalTime.now().plusSeconds((long) Timer_Preset_Offset);
        this.Timer_End = Timer_Start.plusSeconds((long)Timer_Preset_Duration);
        this.Timer_Controller = Timer_Controller;

        boolean TimeCheck = Timer_End.isBefore(Timer_Start);
        Running_C_Duration = Duration.between(Timer_Start, Timer_End).plusHours(TimeCheck ? 24 : 0);
        Running_T_Duration = Running_C_Duration;

        if(Timer_State == State.Delayed){
            Delayed_C_Duration = Duration.between(LocalTime.now(),Timer_Start).plusHours(TimeCheck ? 24 : 0);
            Delayed_T_Duration = Delayed_C_Duration;
        }

        if (DebugMode){
            System.out.println("Timer_Preset_Offset: " + Timer_Preset_Offset + "sec");
            System.out.println("Timer_Preset_Duration: " + Timer_Preset_Duration + "sec");
            System.out.println("Timer Start: " + Timer_Start.format(Timer_12_Format));
            System.out.println("Timer End: " + Timer_End.format(Timer_12_Format));
            System.out.println("Counting Duration: " + FormatTime());
            System.out.println("Timer State: " + Timer_State);
        }
    }

    /**
     * This method is used to execute commands during the timer.
     */


    private void AllTime(){
        if (Timer_State == State.Delayed) {

            Timer_Controller.DelayedStopWatch();
            Delayed_C_Duration = Delayed_C_Duration.minusMillis(1);

            if(Delayed_C_Duration.getSeconds() < 0){
                EnforceState(State.Running);
                Timer_Controller.UpdatePaReButtons(false);
            }
        }

        if(Timer_State == State.Running){

            Timer_Controller.RunningStopWatch();
            Running_C_Duration = Running_C_Duration.minusMillis(1);

            if(Running_C_Duration.getSeconds() < 0){
                EnforceState(State.Finished);
                Timer_Controller.UpdatePaReButtons(true);
            }
        }

        if(Timer_State == State.Finished){
            // Stop blocking stuff here, and return to the main page(GUI).
            Notification.PlaySound(-10);
            Timer_Controller.FinishedStopWatch();
            Control(Command.Stop);
        }
    }

    /**
     * Formats the timer as (hh:mm:ss).
     * @return Formatted time (hh:mm:ss)
     */
    public String FormatTime(){
        String F;
        if (Running_C_Duration.toHours() >= 1){
            long H = Running_C_Duration.toHours();
            long M = Running_C_Duration.minusHours(H).toMinutes();
            long S = Running_C_Duration.minusHours(H).minusMinutes(M).toSeconds();

            F = String.format("%02d:%02d:%02d", H, M, S);
        } else if (Running_C_Duration.toMinutes() >= 1) {
            long M = Running_C_Duration.toMinutes();
            long S = Running_C_Duration.minusMinutes(M).toSeconds();

            F =  String.format("%02d:%02d", M, S);
        } else if (Running_C_Duration.toSeconds() >= 0) {

            long S = Running_C_Duration.toSeconds();
            F =  String.format("%02d", S);

        }else {
            F = "--";
        }
        return F;
    }

    /**
     * Enforces a state upon the timer.
     * @param EnforceThis The state that the timer will enforce.
     */
    private void EnforceState(State EnforceThis){
        Timer_State = EnforceThis;
    }
    public String getStatus(){
        switch (Timer_State){
            case Running -> { return "Timer is Running."; }
            case Paused -> { return "Timer is Paused."; }
            case Finished -> { return "Timer is Finished"; }
            case Delayed -> { return "Timer Started at: " + Timer_Start.format(Timer_12_Format); }
            default -> throw new IllegalArgumentException("Invalid Time State: " + Timer_State);
        }
    }
    /**
     * Controls the timer.
     * @param command The command to run.
     */
    public void Control(Command command){
        switch (command) {
            case Start -> Start();
            case Stop -> Stop();
            case Pause -> Pause();
            case Resume -> Resume();
            default -> throw new IllegalArgumentException("Command: " + command + " is invalid.");
        }
    }

    /**
     * Starts the timer.
     */
    private void Start(){
        TimeScheduler = Executors.newScheduledThreadPool(1);
        TimeScheduler.scheduleAtFixedRate(this::AllTime, 0, 1, TimeUnit.MILLISECONDS);
    }

    /**
     * Stops the timer.
     */
    private void Stop(){
        TimeScheduler.shutdownNow();
        ForceStopTimer();
    }

    /**
     * Resumes the timer.
     */
    private void Resume(){
        Timer_End = LocalTime.now().plus(Paused_C_Duration.toMillis(), ChronoUnit.MILLIS); // here is where the Pause/Resume bug is.
        Running_C_Duration = Paused_C_Duration;
        EnforceState(State.Running);

        TimeScheduler = Executors.newScheduledThreadPool(1);
        TimeScheduler.scheduleAtFixedRate(this::AllTime, 0, 1, TimeUnit.MILLISECONDS);
    }

    /**
     * Pauses the timer.
     */
    private void Pause(){
        Paused_C_Duration = Duration.between(LocalTime.now(), Timer_End);
        EnforceState(State.Paused);
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
