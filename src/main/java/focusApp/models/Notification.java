package focusApp.models;

import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public class Notification {

    public static HashMap<String, File> SoundList = new HashMap<>(){{
        String soundDir = "src/main/resources/focusApp/Sounds/";
        put("Alarm 1", new File(soundDir + "alarm1.wav"));
        put("Alarm 2", new File(soundDir + "alarm2.wav"));
        put("Alarm 3", new File(soundDir + "alarm3.wav"));
    }};

    private final String Alarm;
    private final float Volume;

    public Notification(String Alarm, float Volume){
        this.Alarm = Alarm;
        this.Volume = Volume;
    }
    private static final String VoiceDirectory = "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory";
    private static Voice SpeakingVoice;

    /**
     * Plays a sound of the supported audio format (wav).
     */
    public  void PlaySound(){
        try {
            File AlarmPath = SoundList.get(Alarm);

            if (AlarmPath.exists()){

                // Get the audio from the file.
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(AlarmPath);
                Clip alarmClip = AudioSystem.getClip();
                alarmClip.open(audioInput);

                // Add in volume control.
                FloatControl VolumeControl = (FloatControl) alarmClip.getControl(FloatControl.Type.MASTER_GAIN);
                VolumeControl.setValue(Volume);

                // Play the audio.
                alarmClip.start();

            } else {
              System.out.println("Alarm File Not Found!");
            }
        } catch (Exception e){
            System.out.println(e);
        }
    }

    public static void SpeakText(String TextToSpeak){

        Thread SpeakThread = new Thread(() -> {
            if (SpeakingVoice != null){ SpeakingVoice.deallocate(); }
            System.setProperty("freetts.voices", VoiceDirectory);
            VoiceManager VM = VoiceManager.getInstance();
            SpeakingVoice = VM.getVoice("kevin16");

            SpeakingVoice.allocate();
            SpeakingVoice.speak(TextToSpeak);
            SpeakingVoice.deallocate();
        });
        SpeakThread.start();
    }
}
