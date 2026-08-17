package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;

import game.capabilities.ClearanceLevel;
import game.capabilities.Purchasable;
import game.economy.Wallet;

/**
 * Abstract base class for all access card tiers.
 * Implements ClearanceLevel so doors can check numeric clearance
 * without instanceof checks or tier-specific abilities.
 *
 * Template Method pattern: onPurchase defines the invariant purchase
 * flow while applyPurchaseSideEffect and createCard are overridden
 * by each subclass to define tier-specific behaviour.
 *
 * OCP: adding a new card tier requires only a new subclass — no
 * changes to this class, Door, or GameAbilities.
 *
 * @author Yong Leng Foong
 * @author Alia
 * @version 3.0
 */
public abstract class AbstractAccessCard extends AbstractItem
        implements Purchasable, ClearanceLevel {

    private final int clearanceLevel;

    /**
     * Constructs an access card with the given display properties,
     * weight, and clearance level.
     *
     * @param name          the display name of this card
     * @param displayChar   the character representing this card on the map
     * @param weight        the weight of this card in inventory units
     * @param clearanceLevel the numeric clearance level this card grants
     */
    protected AbstractAccessCard(String name, char displayChar,
                                 int weight, int clearanceLevel) {
        super(name, displayChar, weight);
        this.clearanceLevel = clearanceLevel;
    }

    /**
     * Returns the clearance level this card grants.
     * Used by doors to check access without instanceof or ability checks.
     *
     * @return clearance level
     */
    @Override
    public int getClearanceLevel() {
        return clearanceLevel;
    }

    /**
     * Template method defining the invariant purchase flow with full transactional
     * integrity:
     * <ol>
     *   <li>Attempts to add a fresh card instance to the buyer's inventory.</li>
     *   <li>If the add fails (e.g. weight limit exceeded), refunds the base price
     *       already deducted by {@link game.actions.PurchaseAction} and returns a
     *       failure message — no credits are lost without receiving the item.</li>
     *   <li>Only on successful add does it apply the tier-specific side-effect
     *       via {@link #applyPurchaseSideEffect}.</li>
     * </ol>
     *
     * <p><b>Atomicity:</b> Either the buyer receives the card <em>and</em> pays
     * for it in full, or the transaction is completely rolled back.</p>
     *
     * @param buyer the actor purchasing this card
     * @param map   the current game map
     * @return a description of the transaction and any side-effects
     */
    @Override
    public final String onPurchase(Actor buyer, GameMap map) {
        AbstractAccessCard card = createCard();
        boolean added = buyer.getInventory().add(card);

        if (!added) {
            Wallet.of(buyer).addCredits(getPurchasePrice());
            return buyer + " cannot carry " + this + " — inventory is full! Purchase refunded.";
        }

        String sideEffect = applyPurchaseSideEffect(buyer, map);
        Wallet wallet = Wallet.of(buyer);
        return buyer + " purchased " + this + ". " + sideEffect +
                "Balance: " + wallet.getCredits() + " credits.";
    }

    /**
     * Hook method: applies the tier-specific consequence of purchasing.
     * Level 1 returns empty string, Level 2 deals damage, Level 3 may
     * apply a hidden fee.
     *
     * @param buyer the actor purchasing the card
     * @param map   the current game map
     * @return description of the side-effect, or empty string if none
     */
    protected abstract String applyPurchaseSideEffect(Actor buyer, GameMap map);

    /**
     * Factory hook: returns a fresh instance of the concrete card type.
     * Called by onPurchase to add the correct subclass to inventory.
     *
     * @return a new instance of this card's concrete type
     */
    protected abstract AbstractAccessCard createCard();
}