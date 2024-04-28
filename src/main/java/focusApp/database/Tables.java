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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
