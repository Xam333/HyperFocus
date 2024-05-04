package focusApp.database;

import focusApp.models.BlockedApplication;

import java.util.List;

public interface IBlockedItemDAO {

    public void addApplication(BlockedApplication blockedApplication);

    /**
     * changes the application name
     * @param id application id
     * @param newName name to replace the current name
     * @return true if change made false otherwise
     */
    public boolean changeApplicationName(int id, String newName);

    /**
     * change the url for the application
     * @param id application id
     * @param newUrl url to replace the current url
     * @return true if change made false otherwise
     */
    public boolean changeApplicationUrl(int id, String newUrl);

    /**
     * changes the application icon url
     * @param id the id of the application to be changed
     * @param newIconUrl the url to the new icon
     * @return true if change made false otherwise
     */
    public boolean changeApplicationIcon(int id, String newIconUrl);

    /**
     * returns the id of an application based on its name or -1 if not found
     * @param name the name of the application
     * @return the id of the application or -1 if not found
     */
    public int getApplicationID(String name);

    /**
     * gets a website base on an id
     * @param id the id of the website
     * @return website class
     */
    public BlockedApplication getApplication(int id);

    public List<BlockedApplication> getAllApplications();
}