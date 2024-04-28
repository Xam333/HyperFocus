package focusApp.database;

import focusApp.models.User;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;

public class UserDAO implements IUserDAO {
    private Connection connection;

    public UserDAO() {
        this.connection = DatabaseConnection.getInstance();
        createTable();
    }

    @Override
    public User addUser(String userName, String password) {
        try {
            if (getUserId(userName) != -1) {
                return null;
            }

            /* insert user into database */
            String query =
                    """
                    INSERT INTO user(userName, password, parentalLock) VALUES(?,?,0)
                    """;
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userName);
            statement.setString(2, password);

            statement.executeUpdate();

            /* attempt to log user in and return user class */
            return login(userName, password);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean getParentalLock(int id) {
       try {
           /* get statement based on user id */

           String query = "SELECT parentalLock FROM user WHERE id = ?";

           PreparedStatement statement = connection.prepareStatement(query);

           statement.setInt(1, id);

           ResultSet result = statement.executeQuery();

           /* check value of parentalLock as sql returns int value */
           if (result != null) {
               return (1 == result.getInt("parentalLock"));
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
       return false;
    }


    public int getUserId(String userName) {
        try {
            String query = "SELECT id FROM user WHERE userName = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userName);

            ResultSet result = statement.executeQuery();

            if (result != null) {
                return result.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public User login(String userName, String password) {
        try {
            /* get user from database */

            String query = "SELECT id FROM user WHERE userName = ? AND password = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userName);
            statement.setString(2, password);

            ResultSet result = statement.executeQuery();

            if (result != null) {
                /* if results is not null then user provided correct password and useranme */
                /* so create and return User class */
                int id = result.getInt("id");

                boolean parentalLock = getParentalLock(id);

                User user = new User(userName, parentalLock);
                user.setId(id);
                user.setParentalLock(parentalLock);

                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createTable() {
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
