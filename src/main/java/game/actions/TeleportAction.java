package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import game.capabilities.Teleportable;

/**
 * A generic action that delegates teleportation to any Teleportable device.
 *
 * Supports three teleportation devices:
 * - TeleportationTube: teleports to a pre-determined destination with a
 *   50% malfunction chance and burns surrounding tiles on arrival
 * - MagicCircle: teleports to a random other circle on the map and
 *   spawns a Flask at the destination
 * - AlienCube: teleports to a chosen random location within the map
 *   and corrupts adjacent tiles at the source with ToxicWaste
 *
 * SRP: this class only handles the action mechanics. All teleportation
 * logic is delegated to the Teleportable device itself.
 *
 * OCP: new teleportable devices can be added without modifying this class.
 *
 * @author Alia Divya Anthony
 * @author Yong Leng Foong
 * @version 1.2
 */
public class TeleportAction extends Action {

    private final Teleportable device;
    private final Location destination;

    /**
     * Constructs a TeleportAction for the given device and destination.
     *
     * @param device      the teleportation device handling the logic
     * @param destination the target location, or null for Magic Circle
     *                    which selects its own destination randomly
     */
    public TeleportAction(Teleportable device, Location destination) {
        this.device = device;
        this.destination = destination;
    }

    /**
     * Executes the teleportation by delegating to the device.
     *
     * @param actor the actor teleporting
     * @param map   the current game map
     * @return description of what happened
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        device.setDestination(destination);
        return device.teleport(actor, map);
    }

    /**
     * Returns a menu description based on the type of teleportation device.
     *
     * @param actor the actor performing the action
     * @return menu description string
     */
    @Override
    public String menuDescription(Actor actor) {
        if (destination == null) {
            return actor + " uses the Magic Circle";
        }
        return actor + " uses " + device.getDeviceLabel() + " to teleport to ("
                + destination.x() + ", " + destination.y() + ")";
    }
}