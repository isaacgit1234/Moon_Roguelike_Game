package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;

import game.capabilities.Sellable;
import game.economy.Wallet;

/**
 * An action that sells a {@link Sellable} item to the Supercomputer
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public class SellAction extends Action {

    private final Item item;
    private final Sellable sellable;

    /**
     * Constructs a SellAction for the given item.
     *
     * @param item the item being sold (used for inventory removal)
     * @param sellable the Sellable contract of the item
     */
    public SellAction(Item item, Sellable sellable) {
        this.item = item;
        this.sellable = sellable;
    }

    /**
     * Executes the sale:
     * <ol>
     *   <li>Calculates price via {@link Sellable#getSellPrice}</li>
     *   <li>Adds credits to the seller's wallet</li>
     *   <li>Removes the item from inventory</li>
     *   <li>Delegates side-effects to {@link Sellable#onSell}</li>
     * </ol>
     *
     * @param seller the actor selling the item
     * @param map   the current game map
     * @return a description of the transaction and its effects
     */
    @Override
    public String execute(Actor seller, GameMap map) {
        Wallet wallet = Wallet.of(seller);
        int price = sellable.getSellPrice(seller);

        wallet.addCredits(price);
        seller.getInventory().remove(item);

        String sideEffect = sellable.onSell(seller, map);
        return seller + " sold " + item + " for " + price + " credit(s). " +
                "Balance: " + wallet.getCredits() + " credits.\n" + sideEffect;
    }

    /**
     * Menu description shown to the player.
     *
     * @param seller actor performing the action
     * @return menu string
     */
    @Override
    public String menuDescription(Actor seller) {
        return seller + " sells " + item + " for " + sellable.getSellPrice(seller) + " credit(s)";
    }
}
