package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import game.actions.CutAction;
import game.capabilities.Cuttable;
import game.capabilities.Purchasable;
import game.economy.Wallet;
import game.status.Burned;

/**
 * A plasma cutter that can cut Cuttable targets in the facility.
 *
 * Purchased from the SuperComputer for 50 worker credits.
 * On purchase, deals 5 damage and applies Burned(1, 5) to the buyer
 * since the cutter is ejected at searing temperatures.
 *
 * Cannot be sold back to SuperComputer.
 *
 * When carried, scans adjacent tiles for Cuttable grounds and the actor's
 * inventory for Cuttable items, exposing a CutAction for each valid target.
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public class PlasmaCutter extends AbstractItem implements Purchasable {

    private static final int PURCHASE_PRICE = 50;
    private static final int PURCHASE_DAMAGE = 5;

    /**
     * Constructs a Plasma Cutter, registering it's weight for inventory capacity enforcement.
     */
    public PlasmaCutter() {
        super("Plasma Cutter", '>', 7);
    }

    /** @return {@value #PURCHASE_PRICE} credits */
    @Override
    public int getPurchasePrice() {
        return PURCHASE_PRICE;
    }


    /**
     * Completes the purchase atomically.
     *
     * <p>The inventory is weight-limited, so {@code add} can fail. The cutter is
     * therefore added <em>first</em>: only if it actually lands in the inventory
     * are the irreversible side effects (damage + burn) applied. If the inventory
     * is full, the price already deducted by {@code PurchaseAction} is refunded
     * and no side effects occur — no partial-state corruption, no silent loss.</p>
     *
     * @param buyer the actor purchasing
     * @param map   the current game map
     * @return description of the transaction (or the refund)
     */
    @Override
    public String onPurchase(Actor buyer, GameMap map) {
        boolean added = buyer.getInventory().add(new PlasmaCutter());
        if (!added) {
            Wallet.of(buyer).addCredits(PURCHASE_PRICE);
            return buyer + " cannot carry the Plasma Cutter — inventory full. "
                    + "Purchase refunded; no credits lost.";
        }
        buyer.hurt(PURCHASE_DAMAGE);
        buyer.addStatus(new Burned());
        return buyer + " purchased the Plasma Cutter for " + PURCHASE_PRICE + " credits. "
                + "Ejected from the chute at searing temperatures — takes " + PURCHASE_DAMAGE
                + " damage and is burning for 5 turns!";
    }

    /**
     * When carried, scans adjacent tiles for Cuttable grounds and the actor's own inventory for Cuttable items.
     * Returns a CutAction for each valid Cuttable target found.
     *
     * @param owner the actor carrying the item
     * @param map the current game map
     * @return list of CutActions for all valid targets
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        Location ownerLocation  = map.locationOf(owner);

        for (Exit exit : ownerLocation.getExits()) {
            Location adjacent = exit.getDestination();
            adjacent.getGround().asCapability(Cuttable.class).ifPresent(cuttable ->
                    actions.add(new CutAction(cuttable, adjacent))
            );
        }

        for (var item : owner.getInventory().getItems()) {
            item.asCapability(Cuttable.class).ifPresent(cuttable ->
                    actions.add(new CutAction(cuttable, ownerLocation))
            );
        }
        return actions;
    }
}
