package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.capabilities.Purchasable;

/**
 * Level 1 access card weighing 1 unit, displayed as ▤.
 * Grants clearance level 1 — opens Aluminium doors.
 * Costs 50 credits. No side-effects on purchase.
 *
 * @author Adrian Kristanto
 * @author Yong Leng Foong
 * @author Alia
 * @version 3.0
 */
public class AccessCardLevel1 extends AbstractAccessCard implements Purchasable {

    private static final int PURCHASE_PRICE = 50;
    private static final int CLEARANCE = 1;

    /**
     * Constructs AccessCardLevel1, registering its weight for inventory capacity enforcement.
     */
    public AccessCardLevel1() {
        super("Access Card (Level 1)", '▤', 1, CLEARANCE);
    }

    /** @return {@value #PURCHASE_PRICE} credits */
    @Override
    public int getPurchasePrice() {
        return PURCHASE_PRICE;
    }

    /**
     * No side-effect for Level 1 — returns an empty string.
     *
     * @param buyer the actor purchasing the card (unused)
     * @param map   the current game map (unused)
     * @return empty string
     */
    @Override
    protected String applyPurchaseSideEffect(Actor buyer, GameMap map) {
        return "";
    }

    /**
     * Returns a fresh Level 1 AccessCard instance for inventory insertion.
     *
     * @return new {@link AccessCardLevel1}
     */
    @Override
    protected AbstractAccessCard createCard() {
        return new AccessCardLevel1();
    }
}