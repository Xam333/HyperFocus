package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class for getting connection to database
 */
public class DatabaseConnection {
    private static Connection instance = null;

    /**
     * creates a connection to the database
     */
    private DatabaseConnection() {
        String url = "jdbc:sqlite:database.db";
        try {
            instance = DriverManager.getConnection(url);
        } catch (SQLException sqlEx) {
            System.err.println();
        }
    }

    /**
     * Gets a connection to the database
     * @return Connection to the database
     */
    public static Connection getInstance() {
        if (instance == null) {
            new DatabaseConnection();
        }
        return instance;
    }
}
