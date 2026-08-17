package game.ground;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A dense wall of fog that temporarily blocks movement.
 *
 * <p>FogWall makes previously-passable {@link Floor} terrain temporarily
 * impassable, reshaping movement corridors across the map. After {@link #LIFETIME}
 * turns it self-destructs, restoring the original ground beneath it.</p>
 *
 * <p>This mirrors the pattern established by {@link Fire} - temporary ground with
 * a countdown and a stored original ground reference - so the same idiom is applied
 * consistently.</p>
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public class FogWall extends Ground {

    private static final int LIFETIME = 5;

    private int age = 0;
    private final Ground originalGround;
    private final Display display = new Display();

    /**
     * Constructs a FogWall that will restore the given ground on expiry.
     *
     * @param originalGround the ground tile to restore when the fog disperses.
     */
    public FogWall(Ground originalGround) {
        super('F', "Fog Wall");
        this.originalGround = originalGround;
    }

    /**
     * No actor may enter a FogWall tile - it is physically impassable.
     *
     * @param actor the actor attempting to enter
     * @return always false
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Ages the fog each turn. When the countdown expires the original ground
     * is restored.
     *
     * @param location this tile's current location
     */
    @Override
    public void tick (Location location) {
        super.tick(location);
        age++;
        if (age >=  LIFETIME) {
            location.setGround(originalGround);
            display.println("The fog wall at (" + location.x() + ", " + location.y() + ") disperses.");
        }
    }
}
