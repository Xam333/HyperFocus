import database.User;

// Import JUnit
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {
    private User user;

    // Before each test, create user John123
    @BeforeEach
    public void setUp() {
        user = new User("John123", false);
    }

    // Test getId
    @Test
    public void testGetId() {
        user.setId(1);
        assertEquals(1, user.getId());
    }

    // Test getParentalLock
    @Test
    public void testGetParentalLock() {
        assertFalse(user.getParentalLock());
    }

    // Test setParentalLock
    @Test
    public void testSetParentalLock() {
        user.setParentalLock(true);
        assertTrue(user.getParentalLock());
    }

}
