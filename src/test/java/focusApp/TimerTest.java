package focusApp;

import focusApp.models.Timer;
import focusApp.controllers.TimerController;

import static org.junit.jupiter.api.Assertions.*;

import focusApp.models.TimerState;
import org.junit.jupiter.api.*;

import java.time.Duration;

class TimerTest {
     private Timer timer;
     private TimerController timerController;

     @BeforeEach
     void setUp() {
         timerController = new TimerController();
         // Initialize the timer with preset values for offset and duration
         timer = new Timer(5.0, 10.0, timerController);
     }

     @AfterEach
     void tearDown() {
         // Clean up any resources if needed
         timer = null;
         timerController = null;
     }

     @Test
     void testGetTimerState() {
         assertEquals(TimerState.Delayed, timer.getTimerState());
     }

     @Test
     void testGetTimer_Preset_Offset() {
         assertEquals(5.0 * 60, timer.getTimer_Preset_Offset());
     }

     @Test
     void testGetTimer_Preset_Duration() {
         assertEquals(10.0 * 60, timer.getTimer_Preset_Duration());
     }

     @Test
     void testFormatTime() {
         // Simulate a running timer for 2 hours, 30 minutes, and 45 seconds
         timer.Running_Counting_Duration = Duration.ofHours(2).plusMinutes(30).plusSeconds(45);
         assertEquals("02:30:45", timer.FormatTime());
     }
}
