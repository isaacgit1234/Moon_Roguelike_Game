package game.capabilities;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;

import game.actions.SellAction;
import game.ground.SuperComputer;

/**
 * Contract for any item that can be sold at the {@link SuperComputer}.
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public interface Sellable {
    /**
     * Returns the sell price for this item. May be dynamic (e.g. Cookies, Lantern).
     *
     * @param seller the actor selling the item
     * @return the credit value of this item at time of sale
     */
    int getSellPrice(Actor seller);

    /**
     * Applies all side-effects triggered by selling this item.
     * Called by {@link game.actions.SellAction} immediately after credits are paid.
     *
     * @param seller the actor selling the item
     * @param map the current game map
     * @return a description of what happened
     */
    String onSell(Actor seller, GameMap map);

    /**
     * Exposes a {@link game.actions.SellAction} when this item is carried
     * in inventory and the actor is standing on the Supercomputer.
     * Eliminates {@code instanceof Sellable} checks in
     * {@link game.ground.SuperComputer} (OCP, DRY).
     *
     * @param owner the actor carrying this item
     * @param map   the current game map
     * @return ActionList containing a SellAction, always non-empty
     */
    default ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        actions.add(new SellAction((Item) this, this));
        return actions;
    }
}
