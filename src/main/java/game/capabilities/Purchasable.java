package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.ground.SuperComputer;

/**
 * Contract for any item that can be purchased at the {@link SuperComputer}.
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public interface Purchasable {
    /**
     * Returns the cost for this item. Some items may apply additional fees
     * inside {@link #onPurchase}.
     *
     * @return base cost in credits
     */
    int getPurchasePrice();

    /**
     * Applies all side-effects triggered by purchasing this item, and add the item
     * to buyer's inventory.
     * Called by {@link game.actions.PurchaseAction} immediately after base price deducted.
     *
     * @param buyer the actor buying the item
     * @param map the current game map
     * @return a description of what happened
     */
    String onPurchase(Actor buyer, GameMap map);

    /**
     * Returns the worst-case total cost this item may charge, including any
     * probabilistic side-effect fees applied inside {@link #onPurchase}.
     *
     * <p>The default implementation returns {@link #getPurchasePrice()} — i.e. no
     * additional charges. Items with optional extra fees (e.g. AccessCardLevel3's
     * hidden 50-credit charge) should override this to return the maximum possible
     * total so that {@link game.actions.PurchaseAction} can validate affordability
     * before any credits are deducted.</p>
     *
     * <p><b>OCP:</b> New items with extra fees only need to override this method —
     * PurchaseAction never needs modification.</p>
     *
     * @return maximum total credits that could be charged for this purchase
     */
    default int getTotalMaxCost() {
        return getPurchasePrice();
    }

    /**
     * Returns true if this item has special behaviour when buyer can't afford it
     * Default is false — most items just reject the purchase
     * @return description of what happened, or null if just show generic message
      */
    default String onCannotAfford(Actor buyer, GameMap map) {
        return null;
    }
}
