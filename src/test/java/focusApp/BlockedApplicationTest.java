package focusApp;

import focusApp.models.BlockedApplication;

// Import JUnit
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BlockedApplicationTest
{
    private BlockedApplication blockedApplication;

    @BeforeEach
    public void setUp() {
        blockedApplication = new BlockedApplication("icon", "name", "location");
    }

    @Test
    public void testGetIconColumn() {
        assertEquals("icon", blockedApplication.getIconColumn());
    }

    @Test
    public void testGetNameColumn() {
        assertEquals("name", blockedApplication.getNameColumn());
    }

    @Test
    public void testGetLocationColumn() {
        assertEquals("location", blockedApplication.getLocationColumn());
    }

    @Test
    public void testSetIconColumn() {
        blockedApplication.setIconColumn("newIcon");
        assertEquals("newIcon", blockedApplication.getIconColumn());
    }

    @Test
    public void testSetNameColumn() {
        blockedApplication.setNameColumn("newName");
        assertEquals("newName", blockedApplication.getNameColumn());
    }

    @Test
    public void testSetLocationColumn() {
        blockedApplication.setLocationColumn("newLocation");
        assertEquals("newLocation", blockedApplication.getLocationColumn());
    }

    @Test
    public void testGetId() {
        blockedApplication.setId(1);
        assertEquals(1, blockedApplication.getId());
    }

    @Test
    public void testSetId() {
        blockedApplication.setId(2);
        assertEquals(2, blockedApplication.getId());
    }
}
