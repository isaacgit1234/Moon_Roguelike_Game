package game.capabilities;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import game.actions.ConsumeAction;

/**
 * Contract for any item that can be consumed by an actor.
 *
 * <p>Provides two default {@code allowableActions} methods — one for when
 * the item is carried in inventory, one for when it lies on the ground.
 * Both delegate to a shared private helper {@link #consumeActions()} to
 * avoid duplicating the same logic (DRY).</p>
 *
 * <p>Both methods expose a {@link ConsumeAction} automatically via the
 * engine's action collection system, eliminating the need for
 * {@code instanceof Consumable} checks in any actor or behaviour class
 * (OCP, DRY).</p>
 *
 * @author Yong Leng Foong
 * @version 1.2
 */
public interface Consumable {

    /**
     * Consumes the item, applying its effect to the actor.
     *
     * @param actor the actor consuming the item
     * @return a string description of the consumption result
     */
    String consume(Actor actor);

    /**
     * Returns whether the item is ready to be consumed.
     *
     * @return true if the item can currently be consumed, false otherwise
     */
    boolean canConsume();

    /**
     * Returns the menu description for consuming this item.
     *
     * @param actor the actor consuming this item
     * @return menu description string
     */
    String getMenuDescription(Actor actor);

    /**
     * Exposes a {@link ConsumeAction} when this item is carried in inventory.
     * Called automatically by the engine each turn via
     * {@code World#prepareAllowableActions}.
     *
     * @param owner the actor carrying this item
     * @param map   the current game map
     * @return ActionList containing a ConsumeAction if ready, empty otherwise
     */
    default ActionList allowableActions(Actor owner, GameMap map) {
        return consumeActions();
    }

    /**
     * Exposes a {@link ConsumeAction} when this item is lying on the ground.
     * Called automatically by the engine each turn, allowing
     * {@link game.behaviours.ConsumeBehaviour} to find consumable items
     * without {@code instanceof} checks.
     *
     * @param location the location this item is lying on
     * @return ActionList containing a ConsumeAction if ready, empty otherwise
     */
    default ActionList allowableActions(Location location) {
        return consumeActions();
    }

    /**
     * Shared helper that builds the ActionList for both inventory and ground
     * contexts. Written once to avoid duplicating the same logic across both
     * {@code allowableActions} overloads (DRY).
     *
     * @return ActionList containing a ConsumeAction if ready, empty otherwise
     */
    private ActionList consumeActions() {
        ActionList actions = new ActionList();
        if (canConsume()) {
            actions.add(new ConsumeAction(this));
        }
        return actions;
    }
}