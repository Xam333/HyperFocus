package focusApp.database;

import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                Preset presets = new Preset(result.getInt("presetID"), result.getString("presetName"));

                output.add(presets);
            }

            return output;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Preset addPreset(int userID, String presetsName) {
        try {
            String query = "INSERT INTO presets(userID, presetName) VALUES(?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, userID);
            statement.setString(2, presetsName);

            statement.execute();

            int id = getLastPrest(userID);

            return new Preset(id, presetsName);
        } catch (SQLiteException sqlex) {
            if (sqlex.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE) {
                return null;
            }
            sqlex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addWebsitePreset(int presetID, int websiteID) {
        try {
            String query = "INSERT INTO presetsToWebsite(presetID, websiteID) VALUES(?,?)";
            PreparedStatement statement = connection.prepareStatement(query);

           statement.setInt(1, presetID);
           statement.setInt(2, websiteID);

           return statement.execute();

        } catch (SQLiteException sqlex) {
            if (sqlex.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE) {
                return false;
            }
            sqlex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addApplicationPreset(int presetID, int applicationID) {
        try {
            String query = "INSERT INTO presetsToApplication(presetID, applicationID) VALUES(?,?)";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, presetID);
            statement.setInt(2, applicationID);

            return statement.execute();

        } catch (SQLiteException sqlex) {
            if (sqlex.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_PRIMARYKEY) {
                return false;
            }
            sqlex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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

    int getLastPrest(int userID) {
        try {
            String query = "SELECT presetID FROM presets WHERE userID = ? ORDER BY presetID desc";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, userID);

            ResultSet res = statement.executeQuery();

            if (res.next()) {
                return res.getInt("presetID");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
