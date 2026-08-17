package game.ground;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

import game.actions.TeleportAction;
import game.capabilities.Teleportable;
import game.items.Flask;

import java.util.*;

/**
 * A mystical ground tile that teleports workers to another circle on the same map.
 * Three circles are placed on the 20-overflow moon. When a worker stands on one,
 * they are instantly transported to a randomly chosen other circle, and a Flask
 * spawns on an adjacent empty tile at the destination.
 *
 * Design note: ALL_CIRCLES is a static registry shared across all MagicCircle
 * instances. This works correctly for a single map but would require refactoring
 * to a per-map list if circles were placed on multiple maps simultaneously.
 * resetRegistry() must be called in EclipseNebula.initialise() to ensure a clean
 * state on every new game.
 *
 * OCP: teleportation logic is fully encapsulated here. TeleportAction delegates
 * to teleport() without knowing which device it is operating on.
 *
 * @author Alia Divya Anthony
 * @author Yong Leng Foong
 * @version 1.2
 */
public class MagicCircle extends Ground implements Teleportable {

    private static final Map<GameMap, List<Location>> CIRCLE_REGISTRY = new HashMap<>();
    private static final Random RANDOM = new Random();

    private Location selfLocation;

    /**
     * Constructs a Magic Circle ground tile.
     */
    public MagicCircle() {
        super('◎', "Magic Circle");
    }

    /**
     * Registers this circle's location on the first tick.
     * Subsequent ticks are no-ops for registration.
     *
     * @param location this circle's location on the map
     */
    @Override
    public void tick(Location location) {
        super.tick(location);
        if (selfLocation == null) {
            selfLocation = location;
            CIRCLE_REGISTRY.computeIfAbsent(location.map(), k -> new ArrayList<>()).add(location);
        }
    }

    /**
     * Allows actors to walk onto the magic circle tile.
     *
     * @param actor the actor attempting to enter
     * @return always true
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return true;
    }

    /**
     * Offers a teleport action when a worker stands on this circle
     * and at least one other circle exists on the map.
     *
     * @param actor     the actor on or near this tile
     * @param location  this tile's location
     * @param direction empty when standing on it, non-empty when adjacent
     * @return list of available actions
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        if (direction.isEmpty() && !getDestinations().isEmpty()) {
            actions.add(new TeleportAction(this, null)); // null = random, menu handles it
        }
        return actions;
    }

    /**
     * Teleports the actor to a randomly chosen other circle on the same map.
     * Spawns a Flask on the first empty adjacent tile at the destination.
     *
     * @param actor       the actor using the circle
     * @param map         the current game map
     * @return description of what happened
     */
    @Override
    public String teleport(Actor actor, GameMap map) {
        List<Location> others = getDestinations();
        if (others.isEmpty()) {
            return actor + " tries the Magic Circle but there are no other circles!";
        }
        Location dest = others.get(RANDOM.nextInt(others.size()));
        map.moveActor(actor, dest);

        for (Exit exit : dest.getExits()) {
            Location adj = exit.getDestination();
            if (!adj.containsAnActor() && adj.getGround().canActorEnter(actor)) {
                adj.addItem(new Flask());
                break;
            }
        }
        return actor + " is teleported to another Magic Circle! A Flask appears nearby.";
    }

    /**
     * Returns all circle locations except this one.
     * Used by allowableActions to check if teleportation is possible,
     * and by teleport() to choose a destination.
     *
     * @return list of other circle locations
     */
    @Override
    public List<Location> getDestinations() {
        if (selfLocation == null) return new ArrayList<>();
        List<Location> others = new ArrayList<>(
                CIRCLE_REGISTRY.getOrDefault(selfLocation.map(), new ArrayList<>())
        );
        others.remove(selfLocation);
        return others;
    }

    /**
     * Returns the device label used by TeleportAction for menu display.
     *
     * @return device label string
     */
    @Override
    public String getDeviceLabel() {
        return "Magic Circle";
    }

    /**
     * Clears the static circle registry.
     * Must be called in EclipseNebula.initialise() to ensure a clean
     * state on every new game — otherwise circles from a previous run
     * remain registered.
     */
    public static void resetRegistry() {
        CIRCLE_REGISTRY.clear();
    }

    /**
     * Intentionally ignored — MagicCircle selects its destination randomly
     * at teleport time and does not use a pre-set destination.
     *
     * @param destination unused
     */
    @Override
    public void setDestination(Location destination) {
        // MagicCircle picks its destination randomly — this is intentionally ignored
    }
}