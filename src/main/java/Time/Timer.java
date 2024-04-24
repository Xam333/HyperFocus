package Time;

import Notifications.Notification;
import com.example.cab302theleftovers.TimerController;

import java.time.Duration;
import java.time.LocalTime;
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
    private enum Status{
        Running, NotRunning, Paused
    }
    private Status TimerStatus;
    public boolean isTimerRunning() { return TimerStatus == Status.Running; }
    public boolean isTimerNotRunning() { return TimerStatus == Status.NotRunning; }
    public boolean isTimerPaused() { return TimerStatus == Status.Paused; }


    private LocalTime Timer_End;
    private LocalTime Timer_Start;
    private TimerController Timer_Controller;


    private final int Timer_Preset_Offset;
    private final int Timer_Preset_Duration;
    public int getTimer_Preset_Offset() { return Timer_Preset_Offset; }
    public int getTimer_Preset_Duration() { return Timer_Preset_Duration;}

    
    private static ScheduledExecutorService TimeScheduler;
    private Duration PausedDuration;
    private Duration CountingDuration;


    /**
     * Basic constructor for the timer class.
     *
     * @param TimerOffset   The offset of minutes before the timer starts.
     * @param TimerDuration The duration of the timer in minutes (0 to 1439).
     */
    public Timer (int TimerOffset, int TimerDuration, TimerController Timer_Controller){
        Timer_Preset_Offset = TimerOffset;
        Timer_Preset_Duration = TimerDuration;
        TimerStatus = Status.NotRunning;

        this.Timer_Start = LocalTime.now().plusMinutes(TimerOffset);
        this.Timer_End = Timer_Start.plusMinutes(TimerDuration);
        this.Timer_Controller = Timer_Controller;

        if(Timer_End.isBefore(Timer_Start)){
            CountingDuration = Duration.between(Timer_Start, Timer_End).plusHours(24);
        }
        else {
            CountingDuration = Duration.between(Timer_Start, Timer_End);
        }

        System.out.println("Timer Start: " + Timer_Start);
        System.out.println("Timer End: " + Timer_End);
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
        if(CountingDuration.getSeconds() < 0 && TimerStatus == Status.Running){
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



    private void TimerStatusSync(Command command) throws TimerStatusError{
        switch(command){
            case Stop:
                if(TimerStatus == Status.NotRunning){
                    throw new TimerStatusError("Timer is not running.");
                } else {
                    TimerStatus = Status.NotRunning;
                }
                break;

            case Start:
                if(TimerStatus == Status.Running){
                    throw new TimerStatusError("Timer is already running.");
                } else if (TimerStatus == Status.Paused) {
                    throw new TimerStatusError("Timer is Paused, Please use Resume.");
                } else {
                    TimerStatus = Status.Running;
                }
                break;

            case Pause:
                if (TimerStatus == Status.NotRunning) {
                    throw new TimerStatusError("Timer is not running.");
                } else if (TimerStatus == Status.Paused) {
                    throw new TimerStatusError("Timer is already Paused.");
                } else {
                    TimerStatus = Status.Paused;
                }
                break;

            case Resume:
                if(TimerStatus == Status.Running){
                    throw new TimerStatusError("Timer is not paused.");
                } else if (TimerStatus == Status.NotRunning) {
                    throw new TimerStatusError("Timer is not running.");
                } else {
                    TimerStatus = Status.Running;
                }
                break;

            default:
                throw new IllegalArgumentException("Invalid Command: " + command);
        }
    }

    public void Control(Command command){
        try{
            switch (command){
                case Start:
                    Start();
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

    private void Start(){
        System.out.println("Timer Started at: " + LocalTime.now());
        TimerStatusSync(Command.Start);
        TimeScheduler = Executors.newScheduledThreadPool(1);
        TimeScheduler.scheduleAtFixedRate(this::RunningTimer, (Timer_Preset_Offset * 60L), 1, TimeUnit.SECONDS);
    }
    private void Stop(){
        System.out.println("Timer Stopped at: " + LocalTime.now());
        TimerStatusSync(Command.Stop);
        // Get total time here.
        TimeScheduler.shutdownNow();
    }
    private void Resume(){
        System.out.println("Timer Resumed at: " + LocalTime.now());
        TimerStatusSync(Command.Resume);
        Timer_End = LocalTime.now().plusSeconds(PausedDuration.toSeconds());
        CountingDuration = PausedDuration;
        TimeScheduler = Executors.newScheduledThreadPool(1);
        TimeScheduler.scheduleAtFixedRate(this::RunningTimer, 0, 1, TimeUnit.SECONDS);
    }
    private void Pause(){
        System.out.println("Timer Paused at: " + LocalTime.now());
        TimerStatusSync(Command.Pause);
        PausedDuration = Duration.between(LocalTime.now(), Timer_End);
        TimeScheduler.shutdownNow();
    }
}
