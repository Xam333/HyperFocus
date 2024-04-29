package focusApp.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Connection;

public class PresetDAO implements IPresetDAO {
    private Connection connection;

    public PresetDAO() {
        this.connection = DatabaseConnection.getInstance();
        Tables.createTables();
    }

    @Override
    public ArrayList<Preset> getUsersPresets(int userID) {
        try {
            String query = "SELECT presetID, presetName FROM presets WHERE userID = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userID);

            ResultSet result = statement.executeQuery();

            ArrayList<Preset> output = new ArrayList<>();

            while (result.next()) {
                Preset preset = new Preset(result.getInt("presetID"), result.getString("presetName"));

                output.add(preset);
            }

            return output;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<Application> getPresetApplication(int presetID) {
        try {
            String query = """
                    SELECT
                        app.applicationID, applicationName, url, icon FROM applications as app
                    INNER JOIN
                        presetsToApplication as preset ON preset.applicationID = app.applicationID
                    WHERE presetID = ?
                    """;

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, presetID);

            ResultSet result = statement.executeQuery();

            ArrayList<Application> output = new ArrayList<>();

            while (result.next()) {
                Application application = new Application(result.getInt("applicationID"), result.getString("applicationName"), result.getString("url"), result.getString("icon"));

                output.add(application);
            }

            return output;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<Website> getPresetWebsite(int presetID) {
        try {
            String query = """
                    SELECT
                        web.websiteID, websiteName, url, icon FROM websites as web
                    INNER JOIN
                        presetsToWebsite as preset ON preset.websiteID = web.websiteID
                    WHERE presetID = ?
                    """;

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, presetID);

            ResultSet result = statement.executeQuery();

            ArrayList<Website> output = new ArrayList<>();

            while (result.next()) {
                Website website = new Website(result.getInt("websiteID"), result.getString("websiteName"), result.getString("url"), result.getString("icon"));

                output.add(website);
            }

            return output;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
