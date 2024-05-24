package focusApp.database;

import focusApp.models.block.BlockedApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
public class MockedBlockedItemDAO implements IBlockedItemDAO{


    public static final ArrayList<BlockedApplication> blockedItems = new ArrayList<>();
    public static int autoIncrementedID = 0;

    public MockedBlockedItemDAO()
    {
        //Add initial data
        //addApplication(new BlockedApplication("youtubeIcon", "Youtube", "www.youtube.com"));
        //addApplication(new BlockedApplication("redditIcon", "Reddit", "www.reddit.com"));

    }

    @Override
    public void addApplication(BlockedApplication blockedApplication) {
        blockedApplication.setId(autoIncrementedID);
        autoIncrementedID++;
        blockedItems.add(blockedApplication);
    }

    @Override
    public void updateContact(BlockedApplication blockedApplication) {
        for (int i = 0; i < blockedItems.size(); i++) {
            if (blockedItems.get(i).getId() == blockedApplication.getId()) {
                blockedItems.set(i, blockedApplication);
                break;
            }
        }
    }

    @Override
    public void deleteContact(BlockedApplication blockedApplication) {
        blockedItems.remove(blockedApplication);
    }

    @Override
    public BlockedApplication getBlockedApplication(int id) {
        for (BlockedApplication blockedApplication : blockedItems) {
            if (blockedApplication.getId() == id) {
                return blockedApplication;
            }
        }
        return null;
    }

    @Override
    public List<BlockedApplication> getAllApplications() {
        return new ArrayList<>(blockedItems);
    }

    @Override
    public ObservableList<BlockedApplication> dataList(List<BlockedApplication> applications) {
        return FXCollections.observableArrayList(applications);
    }
}
