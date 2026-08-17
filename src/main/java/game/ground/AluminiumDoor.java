package game.ground;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import game.capabilities.Cuttable;
import game.items.AluminiumScrap;

import java.util.Random;

/**
 * An Aluminium door requiring clearance level 1 or higher.
 *
 * <p>Retains its subclass solely because it implements {@link Cuttable} —
 * unique behaviour not shared by other door tiers. All door configuration
 * (clearance level, display character, unlock side-effect) is passed up
 * to the fully parameterised {@link Door} constructor.</p>
 *
 * <p>IronDoor and TitaniumDoor no longer exist as subclasses — they are
 * constructed directly as {@link Door} instances in {@code EclipseNebula}.</p>
 *
 * @author Yong Leng Foong
 * @version 3.0
 */
public class AluminiumDoor extends Door implements Cuttable {

    private static final int SHOCK_DAMAGE = 2;
    private static final int REQUIRED_CLEARANCE = 1;

    /**
     * Constructs an Aluminium Door, injecting the shock side-effect
     * as an {@link UnlockEffect} lambda.
     */
    public AluminiumDoor() {
        super('=', "Aluminium Door", REQUIRED_CLEARANCE,
                (actor, location) -> {
                    actor.hurt(SHOCK_DAMAGE);
                    return "Electrical short-circuit! " + actor +
                            " takes " + SHOCK_DAMAGE + " damage.";
                },
                "unlock the Aluminium Door");
    }

    /**
     * Cuts the Aluminium Door, dropping Aluminium Scrap on the floor
     * and transforming the tile into a Floor.
     * 25% chance of explosion dealing 100 damage to all adjacent entities.
     *
     * @param actor    the actor performing the cut
     * @param location the location of this door
     * @param map      the current game map
     * @return description of what happened
     */
    @Override
    public String onCut(Actor actor, Location location, GameMap map) {
        location.addItem(new AluminiumScrap());
        location.setGround(new Floor());

        if (new Random().nextDouble() < 0.25) {
            StringBuilder result = new StringBuilder(
                    actor + " cuts the Aluminium Door! BOOM! The door explodes!\n");
            for (Exit exit : location.getExits()) {
                Actor target = exit.getDestination().getActor();
                if (target != null) {
                    target.hurt(100);
                    result.append(target).append(" takes 100 damage from the explosion!\n");
                }
            }
            return result.toString();
        }
        return actor + " cuts the Aluminium Door! Aluminium Scrap dropped. Door becomes Floor.";
    }
}