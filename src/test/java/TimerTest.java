import focusApp.models.Command;
import focusApp.models.Timer;
import focusApp.controllers.TimerController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.TimeUnit;

 class TimerTest {
     private Timer timer;
     private TimerController mockController;

     @BeforeEach
     void setup() {
         // Mock TimerController for isolated testing.
         mockController = mock(TimerController.class);
     }

     // Test Timer constructor
     @Test
     void testTimerConstructor() {
         // Test timer constructor with non-null values
         timer = new Timer(1.0, 5.0, mockController);
         assertEquals(60, timer.getTimer_Preset_Offset(), 0.01);        // Offset in seconds
         assertEquals(300, timer.getTimer_Preset_Duration(), 0.01);     // Duration in seconds
         assertTrue(timer.isTimerNotRunning());

         // Test timer constructor with null values
         timer = new Timer(null, null, mockController);
         assertEquals(0, timer.getTimer_Preset_Offset(), 0.01);         // Offset in seconds
         assertEquals(0, timer.getTimer_Preset_Duration(), 0.01);       // Duration in seconds
         assertTrue(timer.isTimerNotRunning());
     }

     // Test Timer start with 0 offset
     @Test
     void testTimerStart_NoOffset() {
         // Construct timer with 0 offset
         timer = new Timer(0.0, 5.0, mockController);
         timer.Control(Command.Start);          // Start the timer

         assertTrue(timer.isTimerRunning());    // Timer should be running immediately
     }

     // Test Timer start with 1 second offset
     @Test
     void testTimerStart_WithOffset() throws InterruptedException {
         // Construct timer with a short offset (0.016 minutes = 1 second)
         timer = new Timer(0.016, 5.0, mockController);
         timer.Control(Command.Start);

         assertTrue(timer.isTimerPreRunning());     // Should be in pre run state

         TimeUnit.SECONDS.sleep(1);         // Wait for 6 seconds

         assertTrue(timer.isTimerRunning());        // Should now be running
     }

     @Test
     void testTimerPause() {
         timer = new Timer(0.0, 5.0, mockController);  // Immediate start timer
         timer.Control(Command.Start);  // Start the timer
         timer.Control(Command.Pause);  // Pause the timer

         assertTrue(timer.isTimerPaused());  // Timer should be paused
         assertFalse(timer.isTimerRunning());  // Timer should not be running
     }

     @Test
     void testTimerResume() {
         timer = new Timer(0.0, 5.0, mockController);  // Immediate start timer
         timer.Control(Command.Start);  // Start the timer
         timer.Control(Command.Pause);  // Pause the timer
         timer.Control(Command.Resume);  // Resume the timer

         assertTrue(timer.isTimerRunning());  // Timer should be running again
     }
}
