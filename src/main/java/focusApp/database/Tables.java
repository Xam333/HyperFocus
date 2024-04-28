package focusApp.database;

import javafx.scene.chart.PieChart;
import jdk.dynalink.beans.StaticClass;

import java.sql.Statement;
import java.sql.Connection;

public class Tables {
    public static void createTables() {

        Connection connection = DatabaseConnection.getInstance();

        try {
            Statement statement = connection.createStatement();
            String query = """
                    CREATE TABLE IF NOT EXISTS user(
                        id INTEGER PRIMARY KEY,
                        userName TEXT NOT NULL UNIQUE,
                        password TEXT NOT NULL,
                        parentalLock INTEGER NOT NULL CHECK(parentalLock in (0,1))
                    )
                    """;
            statement.execute(query);

            statement = connection.createStatement();
            query = """
                    CREATE TABLE IF NOT EXISTS presets(
                        presetID INTEGER PRIMARY KEY,
                        userID INTEGER,
                        presetName TEXT NOT NULL,
                        FOREIGN KEY(userID) REFERENCES user(id)
                    )
                    """;
            statement.execute(query);

            statement = connection.createStatement();
            query = """
                    CREATE TABLE IF NOT EXISTS websites(
                        websiteID INTEGER PRIMARY KEY,
                        websiteName TEXT NOT NULL,
                        url TEXT NOT NULL,
                        icon TEXT NOT NULL
                    )
                    """;
            statement.execute(query);

            statement = connection.createStatement();
            query = """
                    CREATE TABLE IF NOT EXISTS applications(
                        applicationID INTEGER PRIMARY KEY,
                        applicationName TEXT NOT NULL,
                        url TEXT NOT NULL,
                        icon TEXT NOT NULL
                    )
                    """;
            statement.execute(query);

            statement = connection.createStatement();
            query = """
                    CREATE TABLE IF NOT EXISTS presetsToWebsite(
                    presetID INTEGER NOT NULL,
                    websiteID INTEGER NOT NULL,
                    PRIMARY KEY(presetID, websiteID),
                    FOREIGN KEY(presetID) REFERENCES presets(presetID),
                    FOREIGN KEY(websiteID) REFERENCES websites(websiteID)
                    )
                    """;
            statement.execute(query);

            statement = connection.createStatement();
            query = """
                    CREATE TABLE IF NOT EXISTS presetsToApplication(
                    presetID INTEGER NOT NULL,
                    applicationID INTEGER NOT NULL,
                    PRIMARY KEY(presetID, applicationID),
                    FOREIGN KEY(presetID) REFERENCES presets(presetID),
                    FOREIGN KEY(applicationID) REFERENCES applications(applicationID)
                    )
                    """;
            statement.execute(query);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
