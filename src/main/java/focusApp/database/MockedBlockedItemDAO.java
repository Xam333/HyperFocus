package focusApp.database;

import focusApp.models.BlockedApplication;

import java.util.ArrayList;
import java.util.List;
public class MockedBlockedItemDAO implements IBlockedItemDAO{


    public static final ArrayList<BlockedApplication> blockedItems = new ArrayList<>();
    public static int autoIncrementedID = 0;

    public MockedBlockedItemDAO()
    {
        //Add initial data
        addApplication(new BlockedApplication("youtubeIcon", "Youtube", "www.youtube.com"));

    }

    @Override
    public void addApplication(BlockedApplication blockedApplication) {
        blockedApplication.setId(autoIncrementedID);
        autoIncrementedID++;
        blockedItems.add(blockedApplication);
    }

    @Override
    public boolean changeApplicationName(int id, String newName) {
        return false;
    }

    @Override
    public boolean changeApplicationUrl(int id, String newUrl) {
        return false;
    }

    @Override
    public boolean changeApplicationIcon(int id, String newIconUrl) {
        return false;
    }

    @Override
    public int getApplicationID(String name) {
        return 0;
    }

    @Override
    public BlockedApplication getApplication(int id) {
        return null;
    }

    @Override
    public List<BlockedApplication> getAllApplications() {
        return new ArrayList<>(blockedItems);
    }
}
