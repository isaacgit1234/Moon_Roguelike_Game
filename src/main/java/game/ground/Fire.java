package game.ground;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

import game.status.Burned;

/**
 * A short-lived hazardous ground spawned by a leaky {@link game.items.Lantern}.
 * The fire burns for 5 turns, then reverts to its captured original ground. Each
 * turn, any actor standing on it receives a {@link Burned} status; if the actor is
 * already burning, the burn is extended (stacks in time) rather than reset, via the
 * shared {@link Burned#applyOrExtend(edu.monash.fit2099.engine.actors.Actor)} helper.
 *
 * @author Yong Leng Foong
 * @version 1.1
 */
public class Fire extends Ground {

    private final int lifetime;
    private int age = 0;
    private final Ground originalGround;

    /**
     * Constructs a Fire ground.
     *
     * @param originalGround the ground to restore after the fire expires
     * @param lifetime       number of turns the fire burns
     */
    public Fire(Ground originalGround, int lifetime) {
        super('^', "Fire");
        this.originalGround = originalGround;
        this.age = 0;
        this.lifetime = lifetime;
    }

    /**
     * Constructs a Fire ground with the default 5-turn lifetime.
     *
     * @param originalGround the ground to restore after the fire expires
     */
    public Fire(Ground originalGround) {
        this(originalGround, 5);
    }

    /**
     * Invoked each turn by the engine. Burns any actor on this tile (extending an
     * existing burn), ages the fire, and restores the original ground once the fire
     * has burned for its full lifetime.
     *
     * @param location this fire's location on the map
     */
    @Override
    public void tick(Location location) {
        super.tick(location);
        if (location.containsAnActor()) {
            Burned.applyOrExtend(location.getActor());
        }
        age++;
        if (age >= lifetime) {
            location.setGround(originalGround);
        }
    }
}