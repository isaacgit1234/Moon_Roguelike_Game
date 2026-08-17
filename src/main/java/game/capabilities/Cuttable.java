package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Contract for any ground or item that can be cut by a PlasmaCutter.
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public interface Cuttable {
    /**
     * Applies all side effects of being cut by the PlasmaCutter
     *
     * @param actor the actor performing the cut
     * @param location the location of the cuttable target
     * @param map the current game map
     * @return description of what happened
     */
    String onCut(Actor actor, Location location, GameMap map);
}
