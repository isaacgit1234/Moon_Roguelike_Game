package game.ground;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 *  A functional interface representing the side-effect triggered when a {@link Door}
 *  is successfully unlocked.
 *
 *  <p>Each {@link Door} subclass injects its tier-specific unlock consequence as a
 *  lambda at construction time rather than overriding {@code unlock()} and duplicating
 *  the lock-state check. {@link Door} fires the effect after a successful unlock,
 *  keeping side-effect logic out of the base class.</p>
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
@FunctionalInterface
public interface UnlockEffect {

    /**
     * No-operation effect used by doors that unlock silently.
     * Returns an empty string so callers can always concatenate the result.
     */
    UnlockEffect NONE = (actor, location) -> "";

    /**
     * Applies the side-effect of unlocking the door.
     *
     * @param actor    the actor who performed the unlock
     * @param location the location of the door that was unlocked
     * @return a description of the side-effect, or an empty string if none
     */
    String apply(Actor actor, Location location);
}