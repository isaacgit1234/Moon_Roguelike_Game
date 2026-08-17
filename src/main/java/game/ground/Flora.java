package game.ground;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

import game.behaviours.FloraBehaviour;
import game.behaviours.GrowthBehaviour;
import game.capabilities.GameAbilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Abstract parent class for all alien flora on the 20-overflow moon.
 * Flora are environmental grounds that can grow and react automatically over turns.
 *
 * @author Yong Leng Foong
 * @author Liviru
 * @version 1.2
 */
public abstract class Flora extends Ground {

    /**
     * Number of turns the flora has existed.
     */
    private int turnsAlive = 0;

    /**
     * Shared random number generator for all flora subclasses.
     * Instantiated once and reused to ensure consistency with the rest of the codebase.
     */
    protected final Random random = new Random();

    private final List<FloraBehaviour> behaviours = new ArrayList<>();

    /**
     * Registers a behaviour to be executed each turn.
     * Called by subclass constructors to compose their behaviour set.
     *
     * @param behaviour the behaviour to register
     */
    protected void addBehaviour(FloraBehaviour behaviour) {
        behaviours.add(behaviour);
    }

    /**
     * Constructor for Flora.
     *
     * @param displayChar character displayed on the map
     * @param name name of the flora
     */
    public Flora(char displayChar, String name) {
        super(displayChar, name);
        enableAbility(GameAbilities.IS_FLORA);
    }

    /**
     * Updates the flora every turn — increments the turn counter then
     * delegates to all registered {@link FloraBehaviour}s.
     *
     * <p>Behaviours are executed in registration order. If a growth behaviour
     * replaces this ground tile, subsequent behaviours on this instance will
     * still be called but will have no effect since the tile no longer holds
     * this flora.</p>
     *
     * @param location current location of the flora
     */
    @Override
    public void tick(Location location) {
        super.tick(location);
        turnsAlive++;
        for (FloraBehaviour behaviour : behaviours) {
            behaviour.operate(this, location);
        }
    }

    /**
     * Returns the number of turns this flora has been alive.
     * Used by {@link GrowthBehaviour} to determine
     * growth intervals without exposing the field directly.
     *
     * @return turns alive
     */
    public int getTurnsAlive() {
        return turnsAlive;
    }

    /**
     * Checks whether a contracted worker exists in any adjacent tile.
     *
     * @param location current location of the flora
     * @return true if a nearby contracted worker exists, otherwise false
     */
    public boolean isWorkerNearby(Location location) {
        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            Actor actor = destination.getActor();
            if (actor != null && actor.hasAbility(GameAbilities.IS_WORKER)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a random empty adjacent location, or null if none exist.
     * Collects ALL valid candidates first before randomly selecting —
     * guarantees fairness and avoids silent no-ops from a single blocked pick.
     *
     * @param location current flora location
     * @return a random empty adjacent location, or null if none exist
     */
    public Location getEmptyAdjacentLocation(Location location) {
        List<Location> candidates = new ArrayList<>();

        for (Exit exit : location.getExits()) {
            Location destination = exit.getDestination();
            if (!destination.containsAnActor()) {
                candidates.add(destination);
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        return candidates.get(random.nextInt(candidates.size()));
    }
}