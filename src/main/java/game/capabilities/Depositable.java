package game.capabilities;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.items.Item;

import game.actions.DepositAction;

/**
 * Contract for any item that can be deposited at the SuperComputer for company credits
 *
 * Mirror the Sellable interface pattern - provides a default allowableActions() so
 * SuperComputer needs zero changes to expose DepositAction to Depositable items.
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public interface Depositable {
    /**
     * Returns the company credit value of this item when deposited.
     *
     * @return company credits awarded on deposit
     */
    int getDepositValue();

    /**
     * Applies all side effects triggered by depositing this item.
     * Called by DepositAction after credits are added to QuotaManager.
     *
     * @param actor the actor depositing the item
     * @param map the current game map
     * @return description of what happened
     */
    String onDeposit(Actor actor, GameMap map);

    /**
     * Exposes a DepositAction when this item is carried in inventory and the actor
     * is standing on the SuperComputer.
     *
     * @param owner the actor carrying the item
     * @param map the current game map
     * @return ActionList containing a DepositAction
     */
    default ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        actions.add(new DepositAction((Item) this, this));
        return actions;
    }

}
