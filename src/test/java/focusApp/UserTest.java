package focusApp;

import focusApp.models.User;

// Import JUnit
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {
    private User user;

    @BeforeEach
    public void setUp() {
        user = new User("TestUser", true);
    }

    @Test
    public void testGetId() {
        user.setId(1);
        assertEquals(1, user.getId());
    }

    @Test
    public void testSetId() {
        user.setId(2);
        assertEquals(2, user.getId());
    }

    @Test
    public void testGetUserName() {
        assertEquals("TestUser", user.getUserName());
    }

    @Test
    public void testSetUserName() {
        user.setUserName("NewUser");
        assertEquals("NewUser", user.getUserName());
    }

    @Test
    public void testSetParentalLock() {
        user.setParentalLock(false);
        assertFalse(user.parentalLock);
    }
}
