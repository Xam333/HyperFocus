package focusApp.models.colour;

import focusApp.models.colour.ColourControl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UserConfig {
    private static final Path ConfigPath = Paths.get(System.getProperty("user.home"), "/HyperFocus/");
    private static final Path CSSFile = Paths.get(String.valueOf(ConfigPath), "stylesheets.css");
    public static Path getCSSFilePath(){ return CSSFile; }
    private enum Config{
        Directory, CSSFile
    }
    public static void SetUpEnvi(){

        boolean Directory = Files.isDirectory(ConfigPath);
        boolean File = Files.exists(CSSFile);

        if (!Directory){
            CreateUserConfig(Config.Directory);
            CreateUserConfig(Config.CSSFile);
        } else if (!File){
            CreateUserConfig(Config.CSSFile);
        }
    }
    private static void CreateUserConfig(Config ConfigType){
        try{
            switch (ConfigType){
                case Directory -> Files.createDirectories(ConfigPath);
                case CSSFile -> SetUpCSSFile();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void SetUpCSSFile() throws IOException {
        Files.createFile(CSSFile);
        ColourControl.InjectBasePalette();
    }
    public static boolean FindCSSFile(){
        return Files.exists(CSSFile);
    }
}
