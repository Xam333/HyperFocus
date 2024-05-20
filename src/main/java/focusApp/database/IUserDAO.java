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
     * updates the users name
     * @param id the id of the user
     * @param newName the new to be updated to
     * @return true if successful, false if not
     */
    public boolean updateName(int id, String newName);

    /**
     * add to the total time
     * @param id the id of the user
     * @param time time to be added
     * @return the new total time, -1 if no user found
     */
    public int addToTotalTime(int id, int time);

    /**
     * get the total time from the db
     * @param id id of the user
     * @return total time
     */
    public int getTotalTime(int id);

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
