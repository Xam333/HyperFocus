package database;

import java.sql.Statement;
import java.sql.Connection;

public class UserDAO implements IUserDAO {
    private Connection connection;

    public UserDAO() {
        this.connection = DatabaseConnection.getInstance();
    }

    @Override
    public User addUser(String userName, String password) {

        return null;
    }

    @Override
    public  int login(String userName, String password) {
        return -1;
    }

    private void createTable() {
        try {
            Statement statement = connection.createStatement();
            String query = """
                    CREATE TABLE IF NOT EXISTS user(
                        id INTEGER PRIMARY KEY,
                        userName TEXT NOT NULL,
                        password TEXT NOT NULL,
                        parentalLock INTEGER NOT NULL CHECK(parentalLock in (0,1)
                    )
                    """;
            statement.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
