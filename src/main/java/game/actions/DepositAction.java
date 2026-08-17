package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.capabilities.Depositable;
import game.quota.QuotaManager;

/**
 * An action that deposits a Depositable item at the SuperComputer
 * for company credits.
 *
 * Mirrors SellAction - deducts item from inventory, adds company credits
 * to QuotaManager, then delegates side effects to the item.
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public class DepositAction extends Action {

    private final Item item;
    private final Depositable depositable;

    /**
     * Constructs a DepositAction for the given depositable item.
     *
     * @param item the item being deposited (used for inventory removal)
     * @param depositable the Depositable contract of the item
     */
    public DepositAction(Item item, Depositable depositable) {
        this.item = item;
        this.depositable = depositable;
    }

    /**
     * Executes the deposit:
     * 1. Adds company credits to QuotaManager
     * 2. Removes item from inventory
     * 3. Delegates side effects to depositable.onDeposit()
     *
     * @param actor the actor depositing the item
     * @param map the current game map
     * @return description of what happened
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        int value = depositable.getDepositValue();
        QuotaManager.getInstance().addCompanyCredits(value);
        actor.getInventory().remove(item);
        String sideEffect = depositable.onDeposit(actor, map);
        return actor + " deposited " + item + " for " + value + " company credits. " +
                " [Company credits: " + QuotaManager.getInstance().getCompanyCredits() +
                "/" + QuotaManager.getInstance().getQuota() + "]\n" + sideEffect;
    }

    /**
     * Menu description shown to the player.
     *
     * @param actor the actor performing the deposit
     * @return menu string
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " deposits " + item + " for " + depositable.getDepositValue();
    }

}
