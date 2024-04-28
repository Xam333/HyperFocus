package focusApp.database;

import focusApp.models.User;

public interface IUserDAO {

    /**
     * adds a user account to db and returns the user class
     * @param userName the users name
     * @param password the users password
     * @return User class
     */
    public User addUser(String userName, String password);

    /**
     * gets the parentalLock value for a given id
     * @param id user id
     * @return parental lock value
     */
    public boolean getParentalLock(int id);

    /**
     * return checks if the user id if exists and returns id or -1 if doesnt exits
     * @param userName
     * @return
     */
    public int getUserId(String userName);

    /**
     * checks the username and password and returns the id
     * @param userName userName of the user
     * @param password password of the user
     * @return user object null if no user found
     */
    public User login(String userName, String password);
}
