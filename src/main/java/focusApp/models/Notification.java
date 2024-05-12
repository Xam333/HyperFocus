package focusApp.models;

import javax.sound.sampled.*;
import java.io.File;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public class Notification {

    private static final String VoiceDirectory = "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory";

    private static Voice SpeakingVoice;

    /**
     * Plays a sound of the supported audio format (wav).
     * @param Volume controls the volume of the sound.
     */
    public static void PlaySound(float Volume){
        try {
            String soundFilePath = "src/main/resources/focusApp/Sounds/alarm1.wav";
            File AlarmPath = new File(soundFilePath);

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
