package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.List;

/**
 * Implemented by items and ground tiles that can teleport an actor.
 *
 * OCP: new teleportable devices can be added without modifying
 * TeleportAction or any existing implementation.
 *
 * @author Alia Divya Anthony
 * @author Yong Leng Foong
 * @version 1.1
 */
public interface Teleportable {

    /**
     * Teleports the actor to the destination.
     *
     * @param actor       the actor teleporting
     * @param map         the current game map
     * @return description of what happened
     */
    String teleport(Actor actor, GameMap map);

    /**
     * Returns the list of available destination locations.
     *
     * @return list of destinations
     */
    List<Location> getDestinations();

    /**
     * Returns a short label describing this device for the action menu.
     * Used by TeleportAction to build the menu description without
     * resorting to instanceof checks.
     *
     * @return device label e.g. "Teleportation Tube", "Alien Cube"
     */
    String getDeviceLabel();

    /**
     * Sets the intended destination before {@link #teleport(Actor, GameMap)} is called.
     * Devices that use a fixed destination (e.g. TeleportationTube, AlienCube) store
     * this value and use it during teleportation. Devices that resolve their own
     * destination randomly (e.g. MagicCircle) may ignore this.
     *
     * @param destination the chosen destination location, or null for random devices
     */
    void setDestination(Location destination);
}