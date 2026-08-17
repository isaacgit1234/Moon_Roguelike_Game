package game.ground;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TeleportAction;
import game.capabilities.Teleportable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A teleportation device that transports actors between registered destinations.
 *
 * <p>When used, the tube has a {@value #MALFUNCTION_CHANCE} chance of malfunctioning
 * and sending the actor to a random walkable location on the destination map instead.
 * Regardless of outcome, adjacent tiles at the landing location are set on fire.</p>
 *
 * <p><b>SRP:</b> Manages only teleportation logic and fire side-effects.</p>
 * <p><b>OCP:</b> New destinations are registered via {@link #addDestination(Location)}
 * without modifying this class.</p>
 * <p><b>DIP:</b> Teleportation behaviour is abstracted behind the {@link Teleportable}
 * interface — callers depend on the abstraction, not this concrete class.</p>
 *
 * @author Alia
 * @author Isaac
 * @version 1.2
 */
public class TeleportationTube extends Ground implements Teleportable {

    private static final double MALFUNCTION_CHANCE = 0.5;
    private static final int FIRE_LIFETIME = 2;
    private static final Random RANDOM = new Random();

    private final List<Location> destinations = new ArrayList<>();
    private Location intendedDestination;

    /**
     * Constructs a TeleportationTube ground tile.
     */
    public TeleportationTube() {
        super('Φ', "Teleportation Tube");
    }

    /**
     * Registers a destination location this tube can teleport actors to.
     *
     * @param location the destination to add
     */
    public void addDestination(Location location) {
        destinations.add(location);
    }

    /**
     * Offers {@link TeleportAction}s for each registered destination when the
     * actor is standing directly on this tile.
     *
     * @param actor     the actor querying for actions
     * @param location  this tube's location
     * @param direction empty when the actor stands on this tile; non-empty otherwise
     * @return a list of available teleport actions, or empty if not standing on tile
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        if (direction.isEmpty()) {
            for (Location dest : destinations) {
                actions.add(new TeleportAction(this, dest));
            }
        }
        return actions;
    }

    /**
     * Executes the teleportation. Has a {@value #MALFUNCTION_CHANCE} chance of sending
     * the actor to a random walkable location instead of the intended destination.
     * Sets adjacent walkable tiles at the landing location on fire regardless of outcome.
     *
     * @param actor the actor being teleported
     * @param map   the current game map
     * @return description of the teleportation result including any malfunction
     */
    @Override
    public String teleport(Actor actor, GameMap map) {
        if (intendedDestination == null) {
            return actor + " tries the Teleportation Tube but no destination is set!";
        }

        GameMap destMap = intendedDestination.map();
        Location finalDest = intendedDestination;
        String malfunctionNote = "";

        if (RANDOM.nextDouble() < MALFUNCTION_CHANCE) {
            finalDest = randomWalkableLocation(destMap, actor);
            malfunctionNote = " (MALFUNCTION — sent to random location!)";
        }

        burnAdjacent(finalDest, actor);
        destMap.moveActor(actor, finalDest);
        intendedDestination = null;

        return actor + " uses the Teleportation Tube!" + malfunctionNote + " Surroundings catch fire!";
    }

    /**
     * Sets the intended destination before teleport() is called.
     * Called by TeleportAction before teleport() is invoked.
     *
     * @param destination the chosen destination location
     */
    @Override
    public void setDestination(Location destination) {
        this.intendedDestination = destination;
    }

    /**
     * Returns a defensive copy of all registered destination locations.
     *
     * @return list of destinations this tube can send actors to
     */
    @Override
    public List<Location> getDestinations() {
        return new ArrayList<>(destinations);
    }

    /**
     * Returns the display label for this teleportation device.
     *
     * @return the device label string
     */
    @Override
    public String getDeviceLabel() {
        return "Teleportation Tube";
    }

    /**
     * Sets adjacent walkable tiles at the given location on fire.
     * Uses canActorEnter as the consistent adjacency rule across all teleport devices.
     *
     * @param location the landing location whose neighbours are burned
     * @param actor    the actor used to determine tile walkability
     */
    private void burnAdjacent(Location location, Actor actor) {
        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();
            if (adj.getGround().canActorEnter(actor)) {
                adj.setGround(new Fire(adj.getGround(), FIRE_LIFETIME));
            }
        }
    }

    /**
     * Finds a random walkable, unoccupied location on the destination map.
     * Falls back to the map origin if no walkable location exists.
     *
     * @param destMap the destination map to search
     * @param actor   the actor who needs to enter the location
     * @return a valid walkable location, or map origin as fallback
     */
    private Location randomWalkableLocation(GameMap destMap, Actor actor) {
        List<Location> walkable = new ArrayList<>();
        for (int x : destMap.getXRange()) {
            for (int y : destMap.getYRange()) {
                Location loc = destMap.at(x, y);
                if (loc.getGround().canActorEnter(actor) && !loc.containsAnActor()) {
                    walkable.add(loc);
                }
            }
        }
        if (walkable.isEmpty()) {
            return destMap.at(destMap.getXRange().min(), destMap.getYRange().min());
        }
        return walkable.get(RANDOM.nextInt(walkable.size()));
    }
}