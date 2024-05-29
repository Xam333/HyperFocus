package focusApp.database;

import focusApp.models.block.BlockedApplication;
import focusApp.models.block.BlockedItem;
import javafx.collections.ObservableList;

import java.util.List;

public interface IBlockedItemDAO {

    public void addApplication(BlockedApplication blockedApplication);


    public void updateContact(BlockedApplication blockedApplication);
    /**
     * Deletes a contact from the database.
     *
     * @param blockedItem The contact to delete.
     */

    public void deleteContact(int userID, BlockedItem blockedItem);

    /**
     * Retrieves a contact from the database.
     * @param id The id of the contact to retrieve.
     * @return The contact with the given id, or null if not found.
     */
    public BlockedApplication getBlockedApplication(int id);

    public List<BlockedApplication> getAllApplications();

    public ObservableList<BlockedApplication> dataList(List<BlockedApplication> blockedApplications);
}