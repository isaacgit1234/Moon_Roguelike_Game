package game.capabilities;

/**
 * Implemented by items that carry a security clearance level.
 * Doors use this interface to determine if an actor can unlock them,
 * avoiding the need for separate ability entries per door tier.
 *
 * OCP: adding a new door tier requires no changes to existing cards or
 * this interface — just a new door class with a higher requiredLevel.
 *
 * @author Alia Divya Anthony
 * @version 1.0
 */
public interface ClearanceLevel {

    /**
     * Returns the clearance level this item grants.
     *
     * @return clearance level (1 = Aluminium, 2 = Iron, 3 = Titanium)
     */
    int getClearanceLevel();
}