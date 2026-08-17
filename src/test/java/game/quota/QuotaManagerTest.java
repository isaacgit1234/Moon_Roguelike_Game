package game.quota;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class QuotaManagerTest {

    @BeforeEach
    public void setUp() {
        QuotaManager.reset();
    }

    @Test
    public void testAddCompanyCredits_NormalValue() {
        QuotaManager manager = QuotaManager.getInstance();
        manager.addCompanyCredits(50);
        assertEquals(50, manager.getCompanyCredits());
    }

    @Test
    public void testTick_BoundaryConditionDeadline() {
        QuotaManager manager = QuotaManager.getInstance();
        int initialTurns = manager.getTurnsRemaining();

        // Count down all turns right up to the boundary limit
        for (int i = 0; i < initialTurns - 1; i++) {
            manager.tick();
        }
        assertEquals(1, manager.getTurnsRemaining());
        assertFalse(manager.isEnded());
    }

    @Test
    public void testAddCompanyCredits_InvalidNegativeValue() {
        QuotaManager manager = QuotaManager.getInstance();
        manager.addCompanyCredits(-100);
        assertEquals(0, manager.getCompanyCredits()); // Invalid input gracefully rejected
    }
}