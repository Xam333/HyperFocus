package focusApp.models;

import javafx.scene.paint.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColourControl {

    private final Path CSSFilePath = Paths.get("src/main/resources/focusApp/stylesheet.css");
    private HashMap<ColourPaletteKeys, Color> CurrentColourPalette = new HashMap<>();
    private String CSSFileContent;
    public ColourControl(){
        boolean FileRead = false;

        try{
            CSSFileContent = new String(Files.readAllBytes(CSSFilePath));
            FileRead = true;

        } catch (IOException E){
            System.out.println(E);
        }

        if(FileRead){
            LoadColourPaletteFromFile();
        }
        else{
            LoadDefaultColourPalette();
        }
    }

    public void LoadDefaultColourPalette() {
        CurrentColourPalette = new HashMap<>() {{
            put(ColourPaletteKeys.Primary, Color.rgb(89, 173, 255));
            put(ColourPaletteKeys.Secondary, Color.rgb(44, 111, 220));
            put(ColourPaletteKeys.Tertiary, Color.rgb(67, 130, 191));
            put(ColourPaletteKeys.Background, Color.rgb(230, 249, 255));
        }};
        SaveColourPaletteToFile();
    }

    public void LoadColourPalette(HashMap<ColourPaletteKeys, Color> ColourPalette) {
        CurrentColourPalette = ColourPalette;
        SaveColourPaletteToFile();
    }
    public HashMap<ColourPaletteKeys, Color> getCurrentColourPalette(){
        return CurrentColourPalette;
    }
    private void LoadColourPaletteFromFile(){
        for (ColourPaletteKeys Key : ColourPaletteKeys.values()){

            String CSSVariable = "-" + Key.name() + "Colour";
            Pattern CSSPattern = Pattern.compile(CSSVariable + ": ([#0-9a-fA-F]+);");
            Matcher Match = CSSPattern.matcher(CSSFileContent);

            if (Match.find()){
                CurrentColourPalette.put(Key,Color.web(Match.group(1)));
            }
        }
        System.out.println("Loaded Palette From File: " + CurrentColourPalette);
    }
    public void SaveColourPaletteToFile() {
        try {

            String NewColourPalette = null;
            Matcher Match;

            for (ColourPaletteKeys Key : CurrentColourPalette.keySet()) {

                String CSSVariable = "-" + Key.name() + "Colour";
                String CSSColour = toHex(Key);

                Pattern CSSPattern = Pattern.compile(CSSVariable + ": ([#0-9a-fA-F]+);");
                Match = NewColourPalette == null ? CSSPattern.matcher(CSSFileContent) : CSSPattern.matcher(Objects.requireNonNull(NewColourPalette));

                if (Match.find()) {
                    NewColourPalette = Match.replaceAll(CSSVariable + ": " + CSSColour + ";");
                }
            }
            Files.write(CSSFilePath, Objects.requireNonNull(NewColourPalette).getBytes());

        } catch (IOException E){
            System.out.println(E);
        }
    }
    public String toHex(ColourPaletteKeys Key){
        Color CurrentColour = CurrentColourPalette.get(Key);

        int Red = (int) (CurrentColour.getRed() * 255);
        int Green = (int) (CurrentColour.getGreen() * 255);
        int Blue = (int) (CurrentColour.getBlue() * 255);

        return String.format("#%02X%02X%02X", Red, Green, Blue);
    }
}
