package game.ground;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Toxic ground created by VoidStalker.
 * @author Yong Leng Foong
 * @version 1.1
 */
public class ToxicWaste extends Ground {

    private static final int DAMAGE = 1;
    private final Display display = new Display();
    /**
     * Constructs a ToxicWaste ground tile.
     */
    public ToxicWaste() {
        super('t', "Toxic Waste");
    }

    /**
     * All actors may enter toxic waste — entry is not blocked.
     *
     * @param actor the actor attempting to enter
     * @return always true
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return true;
    }

    /**
     * Deals {@value #DAMAGE} damage to any actor standing on this tile each turn.
     *
     * @param location this tile's location on the map
     */
    @Override
    public void tick(Location location) {
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            actor.hurt(DAMAGE);
            display.println(actor + " takes " + DAMAGE + " damage from Toxic Waste.");
        }
    }
}