package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import game.capabilities.GameAbilities;

/**
 * Chases the nearest actor with a specified ability and forces them
 * to drop one random item from their inventory each turn.
 *
 * This is a new behaviour — no existing behaviour combines movement
 * toward a target with forced inventory disruption.
 *
 * Used by VoidStalker in HuntingState.
 *
 * OCP: target type is determined by ability, not hardcoded class.
 * DIP: depends on Capability abstraction, not concrete actor types.
 *
 * @author Ashley
 * @version 1.0
 */
public class ChaseBehaviour implements Behaviour<Actor, Action> {

    private final GameAbilities targetAbility;
    private final Random random = new Random();

    /**
     * @param targetAbility the ability that identifies chase targets
     */
    public ChaseBehaviour(GameAbilities targetAbility) {
        this.targetAbility = targetAbility;
    }

    @Override
    public Action operate(Actor actor, Location location) {
        // Check adjacent tiles first
        for (Exit exit : location.getExits()) {
            Location dest = exit.getDestination();
            Actor target = dest.getActor();
            if (target != null && target.hasAbility(targetAbility)) {
                forceItemDrop(target, dest);
                if (dest.canActorEnter(actor)) {
                    return dest.getMoveAction(actor, "toward target", exit.getHotKey());
                }
            }
        }

        // No adjacent target — move toward nearest one
        for (Exit exit : location.getExits()) {
            Location dest = exit.getDestination();
            if (dest.canActorEnter(actor) && hasTargetNearby(dest)) {
                return dest.getMoveAction(actor, "toward target", exit.getHotKey());
            }
        }

        return null;
    }

    /**
     * Forces the target to drop one random item onto their current tile.
     *
     * @param target   the target actor
     * @param location the target's location
     */
    private void forceItemDrop(Actor target, Location location) {
        List<Item> items = new ArrayList<>(target.getInventory().getItems());
        if (!items.isEmpty()) {
            Item dropped = items.get(random.nextInt(items.size()));
            target.getInventory().remove(dropped);
            location.addItem(dropped);
        }
    }

    /**
     * Checks if any adjacent tile of the given location contains a target.
     *
     * @param location the location to check around
     * @return true if a target is adjacent
     */
    private boolean hasTargetNearby(Location location) {
        for (Exit exit : location.getExits()) {
            Actor actor = exit.getDestination().getActor();
            if (actor != null && actor.hasAbility(targetAbility)) {
                return true;
            }
        }
        return false;
    }
}