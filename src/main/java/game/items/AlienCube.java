package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import game.actions.TeleportAction;
import game.actors.Undead;

import game.capabilities.Cuttable;
import game.capabilities.Sellable;
import game.capabilities.Teleportable;
import game.ground.ToxicWaste;
import game.spawning.SpawnService;
import game.status.Poisoned;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * An alien cube found scattered in 20-overflow.
 * Can be picked up and used as a teleportation device from inventory.
 * When used, presents 3 random locations within the current map.
 * Using it corrupts the source location — all adjacent floor tiles become ToxicWaste.
 * Can be sold to the Supercomputer for 25 credits, which spawns an Undead nearby.
 *
 * @author Ashley
 * @version 1.0
 */
public class AlienCube extends AbstractItem implements Teleportable, Sellable, Cuttable {

    private static final int SELL_PRICE = 25;
    private static final int NUM_DESTINATIONS = 3;
    private final Random random = new Random();
    private List<Location> cachedDestinations = null;
    private Location chosenDestination = null;

    /**
     * Constructs an AlienCube, registering its weight for inventory capacity enforcement.
     */
    public AlienCube() {
        super("Alien Cube", '◈', 1);
    }

    /**
     * When carried, offers 3 random teleport destinations within the current map.
     *
     * @param owner the actor carrying the cube
     * @param map   the current game map
     * @return list of teleport actions
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        if (cachedDestinations == null) {
            cachedDestinations = getRandomDestinations(map, owner);
        }
        for (Location dest : cachedDestinations) {
            actions.add(new TeleportAction(this, dest));
        }
        return actions;
    }

    /**
     * Teleports the actor to the destination, corrupting all adjacent floor
     * tiles at the source location with ToxicWaste.
     *
     * @param actor       the actor using the cube
     * @param map         the current game map
     * @return description of what happened
     */
    @Override
    public String teleport(Actor actor, GameMap map) {
        Location source = map.locationOf(actor);

        for (Exit exit : source.getExits()) {
            Location adj = exit.getDestination();
            if (adj.getGround().canActorEnter(actor)) {
                adj.setGround(new ToxicWaste());
            }
        }

        map.moveActor(actor, chosenDestination);
        return actor + " tears through space using the Alien Cube! The source location is corrupted with Toxic Waste.";
    }

    /**
     * Sets the chosen destination before teleport() is called.
     * Called by TeleportAction before teleport() is invoked.
     *
     * @param destination the chosen destination location
     */
    @Override
    public void setDestination(Location destination) {
        this.chosenDestination = destination;
    }

    /**
     * Not used for AlienCube — destinations are generated dynamically in allowableActions.
     */
    @Override
    public List<Location> getDestinations() {
        return new ArrayList<>();
    }

    /**
     * @return the display label {@code "Alien Cube"} shown in the device menu
     */
    @Override
    public String getDeviceLabel() {
        return "Alien Cube";
    }

    /**
     * Collects all walkable, unoccupied tiles on the map, shuffles them, and
     * returns a random subset of size {@link #NUM_DESTINATIONS}.
     *
     * @param map   the game map to scan
     * @param actor the actor whose movement rules determine tile eligibility
     * @return a randomly ordered list of candidate destination locations,
     *         smaller than {@link #NUM_DESTINATIONS} if the map has fewer
     *         valid tiles
     */
    private List<Location> getRandomDestinations(GameMap map, Actor actor) {
        List<Location> walkable = new ArrayList<>();
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                if (loc.getGround().canActorEnter(actor) && !loc.containsAnActor()) {
                    walkable.add(loc);
                }
            }
        }
        Collections.shuffle(walkable, random);
        return walkable.subList(0, Math.min(NUM_DESTINATIONS, walkable.size()));
    }

    /**
     * @return the fixed sell price of this item as defined by {@link #SELL_PRICE}
     */
    @Override
    public int getSellPrice(Actor seller) {
        return SELL_PRICE;
    }

    /**
     * Selling the cube removes it from inventory and spawns an Undead on an adjacent tile.
     *
     * @param seller the actor selling
     * @param map    the current game map
     * @return description of what happened
     */
    @Override
    public String onSell(Actor seller, GameMap map) {
        new SpawnService().spawnNear(map.locationOf(seller), new Undead());
        return "The Supercomputer accepts the Alien Cube. An Undead materialises next to " + seller + "!";
    }

    /**
     * Cuts the Alien Cube from inventory, dropping an Alien Artifact
     * on the floor, removing the cube from the actor's inventory,
     * and inflicting Poison on the worker for 5 turns.
     *
     * @param actor    the actor performing the cut
     * @param location the location of the actor
     * @param map      the current game map
     * @return description of what happened
     */
    @Override
    public String onCut(Actor actor, Location location, GameMap map) {
        location.addItem(new AlienArtifact());
        actor.getInventory().remove(this);

        actor.addStatus(new Poisoned(1, 5));

        return actor + " cuts the Alien Cube! Alien Artifact dropped. " +
                actor + " is poisoned by the alien residue!";
    }
}