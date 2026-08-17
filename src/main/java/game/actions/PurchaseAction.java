package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

import game.capabilities.Purchasable;
import game.economy.Wallet;

/**
 * An action that purchase a {@link Purchasable} item to the Supercomputer
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public class PurchaseAction extends Action {

    private final String itemName;
    private final Purchasable purchasable;

    /**
     * Constructs a PurchaseAction for the given purchasable.
     *
     * @param itemName display name shown in the menu
     * @param purchasable item to purchase
     */
    public PurchaseAction(String itemName, Purchasable purchasable) {
        this.itemName = itemName;
        this.purchasable = purchasable;
    }

    /**
     * Executes the purchase:
     * <ol>
     *   <li>Checks whether the buyer can afford the base price</li>
     *   <li>Deducts the base price from the wallet</li>
     *   <li>Delegates the rest (adding item, side-effects) to
     *      {@link Purchasable#onPurchase}</li>
     * </ol>
     *
     * <p>If insufficient funds, the outcome is defined by the item's
     *  {@code onPurchase}
     * </p>
     *
     * @param buyer the actor buying the item
     * @param map   the current game map
     * @return a description of the transaction and its effects
     */
    @Override
    public String execute(Actor buyer, GameMap map) {
        Wallet wallet = Wallet.of(buyer);

        if (!wallet.canAfford(purchasable.getTotalMaxCost())) {
            String consequence = purchasable.onCannotAfford(buyer, map);
            if (consequence != null) {
                return consequence;
            }
            return buyer + " cannot afford " + itemName + "! Need " +
                    purchasable.getTotalMaxCost() + " credits but only has " +
                    wallet.getCredits() + ".";
        }

        wallet.deductCredits(purchasable.getPurchasePrice());
        return purchasable.onPurchase(buyer, map);
    }

    /**
     * Menu description shown to the player.
     *
     * @param buyer actor performing the action
     * @return menu string
     */
    @Override
    public String menuDescription(Actor buyer) {
        Wallet wallet = Wallet.of(buyer);
        int maxCost = purchasable.getTotalMaxCost();
        String costDisplay = maxCost > purchasable.getPurchasePrice()
                ? purchasable.getPurchasePrice() + " (+" + (maxCost - purchasable.getPurchasePrice()) + " possible fee)"
                : String.valueOf(purchasable.getPurchasePrice());
        return buyer + " buy " + itemName + " for " + costDisplay + " credit(s) " +
                "[Balance: " + wallet.getCredits() + " credits]";
    }
}
