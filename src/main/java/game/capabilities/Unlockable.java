package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * Interface for targets that can be unlocked by actor
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public interface Unlockable {
    /**
     * Unlocks the target
     *
     * @param actor the actor performing unlock action
     * @return description of the result
     */
    String unlock(Actor actor);

    /**
     * Returns a description of this unlockable for menu display
     *
     * @param actor Actor who perform the action
     * @return description string
     */
    String getMenuDescription(Actor actor);
}
