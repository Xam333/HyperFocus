package focusApp.models;

import javafx.scene.image.ImageView;

public class BlockedApplication {
    //private String iconColumn;

    private ImageView iconColumn;
    private String nameColumn;
    private String locationColumn;


    public BlockedApplication(ImageView iconColumn, String nameColumn, String locationColumn) {
        this.iconColumn = iconColumn;
        this.nameColumn = nameColumn;
        this.locationColumn = locationColumn;
    }

    public ImageView getIconColumn() {
        return iconColumn;
    }

    public String getNameColumn() {
        return nameColumn;
    }

    public String getLocationColumn() {
        return locationColumn;
    }
}
