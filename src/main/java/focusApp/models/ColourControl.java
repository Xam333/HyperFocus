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

            for (ColourPaletteKeys Key : CurrentColourPalette.keySet()) {
                Color CurrentColour = CurrentColourPalette.get(Key);

                String Palette_Main_Variable = "-" + Key.name() + "Colour";
                String Palette_Text_Variable = "-" + Key.name() + "TextColour";

                String Palette_Main_Colour = toHex(CurrentColour);
                String Palette_Text_Colour = toHex(getBestTextColour(CurrentColour));

                Pattern Palette_Main_Pattern = Pattern.compile(Palette_Main_Variable + ": ([#0-9a-fA-F]+);");
                Pattern Palette_Text_Pattern = Pattern.compile(Palette_Text_Variable + ": ([#0-9a-fA-F]+);");

                Matcher Palette_Main_Match = NewColourPalette == null ? Palette_Main_Pattern.matcher(CSSFileContent) : Palette_Main_Pattern.matcher(Objects.requireNonNull(NewColourPalette));

                if (Palette_Main_Match.find()) {
                    NewColourPalette = Palette_Main_Match.replaceAll(Palette_Main_Variable + ": " + Palette_Main_Colour + ";");
                }

                Matcher Palette_Text_Match = NewColourPalette == null ? Palette_Text_Pattern.matcher(CSSFileContent) : Palette_Text_Pattern.matcher(Objects.requireNonNull(NewColourPalette));

                if (Palette_Text_Match.find()){
                    NewColourPalette = Palette_Text_Match.replaceAll(Palette_Text_Variable + ": " + Palette_Text_Colour + ";");
                }
            }
            System.out.println();
            Files.write(CSSFilePath, Objects.requireNonNull(NewColourPalette).getBytes());

        } catch (IOException E){
            System.out.println(E);
        }
    }
    private Color getBestTextColour(Color CurrentColour){

        double RedLight = 0.2126 * CurrentColour.getRed();
        double GreenLight = 0.7152 * CurrentColour.getGreen();
        double BlueLight = 0.0722 * CurrentColour.getBlue();

        double TotalLuminance = RedLight + GreenLight + BlueLight;
        double LightThreshold = 0.75;

        return TotalLuminance > LightThreshold ? Color.BLACK : Color.WHITE;
    }
    private String toHex(Color CurrentColour){

        int Red = (int) (CurrentColour.getRed() * 255);
        int Green = (int) (CurrentColour.getGreen() * 255);
        int Blue = (int) (CurrentColour.getBlue() * 255);

        return String.format("#%02X%02X%02X", Red, Green, Blue);
    }
    public String[] getHexFromPalette(ColourPaletteKeys MainKey, ColourPaletteKeys TextKey){
        String MainColour = toHex(CurrentColourPalette.get(MainKey));
        String TextColour = toHex(getBestTextColour(CurrentColourPalette.get(TextKey)));

        return new String[]{MainColour, TextColour};
    }

}
