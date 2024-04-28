package focusApp.database;

import java.util.ArrayList;

public interface IPresetDAO {

    /**
     * function to return the presets associated with a specific user
     * @param userID the id of the user
     * @return a list of presets
     */
    public ArrayList<Preset> getUsersPresets(int userID);

    /**
     * function to return the applications from a specific preset
     * @param presetID id of the preset
     * @return list of applications from preset
     */
    public ArrayList<Application> getPresetApplication(int presetID);

    /**
     * function to return the websites from a specific preset
     * @param presetID
     * @return list of websites from preset
     */
    public ArrayList<Website> getPresetWebsite(int presetID);
}
