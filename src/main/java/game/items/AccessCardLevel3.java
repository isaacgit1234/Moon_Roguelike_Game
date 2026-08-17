package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

import game.capabilities.Purchasable;
import game.economy.Wallet;

import java.util.Random;

/**
 * Level 3 access card weighing 3 units, displayed as ◐.
 * Grants clearance level 3 — opens all door types.
 * Costs 200 credits. 50% chance of hidden 50-credit fee on purchase.
 *
 * @author Yong Leng Foong
 * @author Alia
 * @version 4.0
 */
public class AccessCardLevel3 extends AbstractAccessCard implements Purchasable {

    private static final int PURCHASE_PRICE = 200;
    private static final int CLEARANCE = 3;
    private static final double HIDDEN_FEE_CHANCE = 0.50;
    private static final int HIDDEN_FEE = 50;

    private final Random random = new Random();

    /**
     * Constructs AccessCardLevel3, registering its weight for inventory capacity enforcement.
     */
    public AccessCardLevel3() {
        super("Access Card (Level 3)", '◐', 3, CLEARANCE);
    }

    /** @return {@value #PURCHASE_PRICE} credits */
    @Override
    public int getPurchasePrice() {
        return PURCHASE_PRICE;
    }

    /**
     * Returns the worst-case total cost including the possible hidden fee.
     * PurchaseAction uses this to validate affordability before deducting any credits,
     * preventing a situation where the buyer can pass the base-price gate but then
     * cannot cover the hidden fee that fires inside onPurchase.
     *
     * @return {@value #PURCHASE_PRICE} + {@value #HIDDEN_FEE}
     */
    @Override
    public int getTotalMaxCost() {
        return PURCHASE_PRICE + HIDDEN_FEE;
    }

    /**
     * 50% chance the Supercomputer will hit the buyer with a hidden fee, deducting an additional 50 credits
     * on top of the 200-credit purchase price for Level 2 — returns result of charged hidden fee if random
     * number lower than 50%.
     *
     * @param buyer the actor purchasing the card (unused)
     * @param map   the current game map (unused)
     * @return empty string
     */
    @Override
    protected String applyPurchaseSideEffect(Actor buyer, GameMap map) {
        if (random.nextDouble() < HIDDEN_FEE_CHANCE) {
            Wallet.of(buyer).deductCreditsClamped(HIDDEN_FEE);
            return "PREDATORY PRICING — hidden fee of " + HIDDEN_FEE + " credits deducted! ";
        }
        return "";
    }

    /**
     * Returns a fresh Level 3 AccessCard instance for inventory insertion.
     *
     * @return new {@link AccessCardLevel3}
     */
    @Override
    protected AbstractAccessCard createCard() {
        return new AccessCardLevel3();
    }
}