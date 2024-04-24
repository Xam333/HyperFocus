package Time;

import Notifications.Notification;

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

    public boolean isTimerRunning() { return TimerStatus == Status.Running; }
    public boolean isTimerNotRunning() { return TimerStatus == Status.NotRunning; }
    public boolean isTimerPaused() { return TimerStatus == Status.Paused; }



    private Status TimerStatus;
    public Status getTimerStatus() {
        return TimerStatus;
    }
    private LocalTime End;


    private int TotalTimeFocused = 0;
    public Duration getTotalTimeFocused() {
        if (TimerStatus == Status.NotRunning){
            Duration D = Duration.ZERO;
            D = D.plusSeconds(TotalTimeFocused - 1);
            return D;
        }
        throw new TimerStatusError("Timer is running");
    }




    private final int TimerOffset;
    public int getTimerOffset() {
        return TimerOffset;
    }

    private final int TimerDuration;
    public int getTimerDuration() {
        return TimerDuration;
    }

    private static ScheduledExecutorService TimeScheduler;
    private Duration PausedDuration;

    /**
     * Basic constructor for the timer class.
     * @param Offset The offset of minutes before the timer starts.
     * @param Duration The duration of the timer in minutes.
     */
    public Timer (int Offset, int Duration){
        this.End = (LocalTime.now().plusMinutes(Offset)).plusMinutes(Duration);

        this.TimerOffset = Offset;
        this.TimerDuration = Duration;

        this.TimerStatus = Status.NotRunning;
    }

    /**
     * This method is used to execute commands during the timer.
     */
    private void RunningTimer(){
        TotalTimeFocused++;
        // Do the blocking stuff here.

        // Update GUI stuff here.

        // Check if the timer has ended.
        if(LocalTime.now().isAfter(End) && TimerStatus == Status.Running){
            Control(Command.Stop);
            Duration D = getTotalTimeFocused();

            System.out.println("Total Focus Time: " + D.toMinutesPart() + " min/s\t" + D.toSecondsPart() + " sec");
            // Play alert sound here.
            Notification.PlaySound(-10);
            // Stop blocking stuff here, and return to the main page(GUI).

        }
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
        TimeScheduler.scheduleAtFixedRate(this::RunningTimer, (TimerOffset * 60L), 1, TimeUnit.SECONDS);
    }
    private void Stop(){
        System.out.println("Timer Stopped at: " + LocalTime.now());
        TimerStatusSync(Command.Stop);
        TimeScheduler.shutdownNow();
    }

    private void Resume(){
        System.out.println("Timer Resumed at: " + LocalTime.now());
        TimerStatusSync(Command.Resume);
        End = LocalTime.now().plusSeconds(PausedDuration.toSeconds());
        TimeScheduler = Executors.newScheduledThreadPool(1);
        TimeScheduler.scheduleAtFixedRate(this::RunningTimer, 0, 1, TimeUnit.SECONDS);
    }

    private void Pause(){
        System.out.println("Timer Paused at: " + LocalTime.now());
        TimerStatusSync(Command.Pause);
        PausedDuration = Duration.between(LocalTime.now(), End);
        TimeScheduler.shutdownNow();
    }
}
