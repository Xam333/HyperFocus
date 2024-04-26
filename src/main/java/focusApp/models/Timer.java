package focusApp.models;

import focusApp.controllers.TimerController;

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
     * So for the alarm sound is a different class.
     * \
     * For presets all we need to remember is the Offset and Duration.
     * \
     * For Total time focused we would just have a part in the Account where
     * after the timer ends we would just add the time to what is already there.
     */
    private enum State {
        Running, NotRunning, Paused, PreRun
    }
    private State Timer_State;
    public boolean isTimerRunning() { return Timer_State == State.Running; }
    public boolean isTimerNotRunning() { return Timer_State == State.NotRunning; }
    public boolean isTimerPaused() { return Timer_State == State.Paused; }


    private LocalTime Timer_End;
    private final TimerController Timer_Controller;


    private final double Timer_Preset_Offset;
    private final double Timer_Preset_Duration;
    public double getTimer_Preset_Offset() { return Timer_Preset_Offset; }
    public double getTimer_Preset_Duration() { return Timer_Preset_Duration; }

    
    private static ScheduledExecutorService TimeScheduler;
    private Duration PausedDuration;
    private Duration CountingDuration;

    private final DateTimeFormatter Timer_12_Format = DateTimeFormatter.ofPattern("h:mm:ss a");

    /**
     * Basic constructor for the timer class.
     *
     * @param TimerOffset   The offset of minutes before the timer starts.
     * @param TimerDuration The duration of the timer in minutes (0 to 1439).
     */
    public Timer (Double TimerOffset, Double TimerDuration, TimerController Timer_Controller){
        Timer_Preset_Offset = TimerOffset == null ? 0.0 : (TimerOffset * 60);
        Timer_Preset_Duration = TimerDuration == null ? 0.0 : (TimerDuration * 60);
        Timer_State = State.NotRunning;

        System.out.println("Timer_Preset_Offset: " + Timer_Preset_Offset);
        System.out.println("Timer_Preset_Duration: " + Timer_Preset_Duration);

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

    private String FormatTime(){
        long H = CountingDuration.toHours();
        long M = CountingDuration.minusHours(H).toMinutes();
        long S = CountingDuration.minusHours(H).minusMinutes(M).toSeconds();

        return String.format("%02d:%02d:%02d", H, M, S);
    }

    private void EnforceState(State EnforceThis){
        Timer_State = EnforceThis;
    }

    public void Control(Command command){
        try{
            switch (command){
                case Start:
                    if (Timer_Preset_Offset == 0){
                        Start();
                    }
                    else{
                        PreStart();
                    }

                    break;

                case Stop:
                    Stop();
                    break;

                case Pause:
                    Pause();
                    break;

                case Resume:
                    Resume();
                    break;
            }
        }catch (TimerStatusError E){
            System.out.println(E.getMessage());
        }
    }

    private void PreStart(){
        System.out.println("Timer PreRun at: " + LocalTime.now().format(Timer_12_Format));
        EnforceState(State.PreRun);
        Timer_Controller.UpdateStopWatch(FormatTime());
        TimeScheduler = Executors.newScheduledThreadPool(1);
        TimeScheduler.scheduleAtFixedRate(this::Start, (long)Timer_Preset_Offset, 1, TimeUnit.SECONDS);
    }


    private void Start(){
        System.out.println("Timer Started at: " + LocalTime.now().format(Timer_12_Format));
        if (Timer_Preset_Offset != 0){ TimeScheduler.shutdownNow(); }

        EnforceState(State.Running);

        Timer_Controller.UnlockPauseButton();
        TimeScheduler = Executors.newScheduledThreadPool(1);
        TimeScheduler.scheduleAtFixedRate(this::RunningTimer, 0, 1, TimeUnit.SECONDS);
    }
    private void Stop(){
        System.out.println("Timer Stopped at: " + LocalTime.now().format(Timer_12_Format));
        EnforceState(State.NotRunning);
        // Get total time here.
        TimeScheduler.shutdownNow();
    }
    private void Resume(){
        System.out.println("Timer Resumed at: " + LocalTime.now().format(Timer_12_Format));
        EnforceState(State.Running);
        Timer_End = LocalTime.now().plusSeconds(PausedDuration.toSeconds());
        CountingDuration = PausedDuration;
        TimeScheduler = Executors.newScheduledThreadPool(1);
        TimeScheduler.scheduleAtFixedRate(this::RunningTimer, 0, 1, TimeUnit.SECONDS);
    }
    private void Pause(){
        System.out.println("Timer Paused at: " + LocalTime.now().format(Timer_12_Format));
        EnforceState(State.Paused);
        PausedDuration = Duration.between(LocalTime.now(), Timer_End);
        TimeScheduler.shutdownNow();
    }
}
