package focusApp;

import focusApp.models.BlockedItem;

// Import JUnit
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BlockedItemTest {
    private BlockedItem blockedItem;

    @BeforeEach
    public void setUp() {
        blockedItem = new BlockedItem(1, "TestName", "TestURI", "TestIconURI");
    }

    @Test
    public void testGetID() {
        assertEquals(1, blockedItem.getID());
    }

    @Test
    public void testSetID() {
        blockedItem.setID(2);
        assertEquals(2, blockedItem.getID());
    }

    @Test
    public void testGetName() {
        assertEquals("TestName", blockedItem.getName());
    }

    @Test
    public void testSetName() {
        blockedItem.setName("NewName");
        assertEquals("NewName", blockedItem.getName());
    }

    @Test
    public void testGetURI() {
        assertEquals("TestURI", blockedItem.getURI());
    }

    @Test
    public void testSetURI() {
        blockedItem.setURI("NewURI");
        assertEquals("NewURI", blockedItem.getURI());
    }

    @Test
    public void testGetIconURI() {
        assertEquals("TestIconURI", blockedItem.getIconURI());
    }

    @Test
    public void testSetIconURI() {
        blockedItem.setIconURI("NewIconURI");
        assertEquals("NewIconURI", blockedItem.getIconURI());
    }
}
