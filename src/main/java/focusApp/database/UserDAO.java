package focusApp.database;

import focusApp.models.User;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.sql.*;

public class UserDAO implements IUserDAO {
    private Connection connection;

    public UserDAO() {
        this.connection = DatabaseConnection.getInstance();
        /* method to make all required tables */
        Tables.createTables();
    }

    @Override
    public User addUser(String userName, String password) {
        try {
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

    @Override
    public boolean updateName(int id, String newName) {
        try {
            String query = "UPDATE user SET userName = ? WHERE id = ?";

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, newName);
            statement.setInt(2, id);

            return (statement.executeUpdate() != 0);

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

    @Override
    public boolean setParentalLock(int id, boolean lock) {
        try {
            String query = "UPDATE user SET parentalLock = ? WHERE id = ?";

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setBoolean(1, lock);
            statement.setInt(2, id);

            return (statement.executeUpdate() != 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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

            String query = "SELECT id, parentalLock FROM user WHERE userName = ? AND password = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userName);
            statement.setString(2, password);

            ResultSet result = statement.executeQuery();

            if (!result.isClosed()) {
                /* if results is not null then user provided correct password and useranme */
                /* so create and return User class */
                int id = result.getInt("id");
                boolean parentalLock = result.getBoolean("parentalLock");

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
}
