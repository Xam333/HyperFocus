package database;

import java.sql.Connection;

public interface IUserDAO {

    /**
     * adds a user account to db and returns the user class
     * @param userName the users name
     * @param password the users password
     * @return User class
     */
    public User addUser(String userName, String password);

    /**
     * checks the username and password and returns the id
     * @param userName userName of the user
     * @param password password of the user
     * @return the id of the user or -1 if not found
     */
    public int login(String userName, String password);
}
