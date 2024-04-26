package focusApp.models;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;


public class Notification {
    /**
     * Plays a sound of the supported audio format (wav).
     * @param Volume controls the volume of the sound.
     */
    public static void PlaySound(float Volume){
        // This will read from the DB or from a DB information class.
        String SoundFilePath = "src/main/resources/focusApp/Sounds/alarm1.wav";

        try{
            AudioInputStream AIS = AudioSystem.getAudioInputStream(new File(SoundFilePath));
            AudioFormat AF = AIS.getFormat();

            DataLine.Info I = new DataLine.Info(SourceDataLine.class, AF);
            SourceDataLine L = (SourceDataLine) AudioSystem.getLine(I);

            L.open(AF);
            L.start();

            FloatControl VolumeControl = (FloatControl) L.getControl(FloatControl.Type.MASTER_GAIN);
            VolumeControl.setValue(Volume);


            int bufferSize = (int) AF.getSampleRate() * AF.getFrameSize();
            byte[] buffer = new byte[bufferSize];
            int bytesRead;
            while ((bytesRead = AIS.read(buffer, 0, buffer.length)) != -1){
                L.write(buffer, 0, bytesRead);
            }

            L.drain();
            L.close();
            AIS.close();
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }
}
