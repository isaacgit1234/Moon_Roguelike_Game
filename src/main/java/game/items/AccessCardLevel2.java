package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.capabilities.Purchasable;

/**
 * Level 2 access card weighing 2 units, displayed as α.
 * Grants clearance level 2 — opens Aluminium and Iron doors.
 * Costs 100 credits. Deals 5 damage as a blood sample on purchase.
 *
 * @author Yong Leng Foong
 * @author Alia
 * @version 3.0
 */
public class AccessCardLevel2 extends AbstractAccessCard implements Purchasable {

    private static final int PURCHASE_PRICE = 100;
    private static final int CLEARANCE = 2;
    private static final int CALIBRATION_DAMAGE = 5;

    /**
     * Constructs AccessCardLevel2, registering its weight for inventory capacity enforcement.
     */
    public AccessCardLevel2() {
        super("Access Card (Level 2)", 'α', 2, CLEARANCE);
    }

    /** @return {@value #PURCHASE_PRICE} credits */
    @Override
    public int getPurchasePrice() {
        return PURCHASE_PRICE;
    }

    /**
     * Supercomputer forcefully extract blood sample for Level 2 — returns result of dealing 5 damage to buyer.
     *
     * @param buyer the actor purchasing the card (unused)
     * @param map   the current game map (unused)
     * @return empty string
     */
    @Override
    protected String applyPurchaseSideEffect(Actor buyer, GameMap map) {
        buyer.hurt(CALIBRATION_DAMAGE);
        return "Blood sample extracted: -" + CALIBRATION_DAMAGE + " HP. ";
    }

    /**
     * Returns a fresh Level 2 AccessCard instance for inventory insertion.
     *
     * @return new {@link AccessCardLevel2}
     */
    @Override
    protected AbstractAccessCard createCard() {
        return new AccessCardLevel2();
    }

}