package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents something that can be infected.
 *
 * @author Kumali Wickremasinghe
 * @version 1.1
 */
public interface Infectable {

    /**
     * Applies infection behaviour.
     *
     * @param source the actor performing infection
     * @param location location of target
     * @param map current game map
     * @return infection result description
     */
    String infect(Actor source,
                  Location location,
                  GameMap map);
}