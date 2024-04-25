package Time;

public class TimerStatusError extends RuntimeException {
    public TimerStatusError(String Message){
        super("Invalid Timer Status: " + Message);
    }
}
