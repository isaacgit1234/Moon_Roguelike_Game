package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

import game.capabilities.Sellable;
import game.economy.Wallet;

import java.util.Random;

/**
 * A piece of ancient technology weighing 1 unit.
 *
 * <p><b>Selling:</b> Sells for 1 credit. There is a 50% chance the Supercomputer
 * glitches and deducts 50 credits from the seller's wallet after paying out.</p>
 *
 * @author Yong Leng Foong
 * @version 1.1
 */
public class FloppyDisk extends AbstractItem implements Sellable {

    private static final int SELL_PRICE = 1;
    private static final double GLITCH_CHANCE = 0.50;
    private static final int GLITCH_PENALTY = 50;

    private final Random random = new Random();

    /**
     * Constructs a Floppy Disk, registering its weight for inventory capacity
     * enforcement.
     */
    public FloppyDisk() {
        super("Floppy Disk", '⊟', 1);
    }

    // ── Sellable ──────────────────────────────────────────────────────────────

    /** @return fixed price of {@value #SELL_PRICE} credit */
    @Override
    public int getSellPrice(Actor seller) {
        return SELL_PRICE;
    }

    /**
     * 50% chance the Supercomputer glitches and deducts {@value #GLITCH_PENALTY}
     * credits from the seller after paying out.
     *
     * @param seller the actor selling the Floppy Disk
     * @param map    the current game map (unused)
     * @return description of the outcome
     */
    @Override
    public String onSell(Actor seller, GameMap map) {
        if (random.nextDouble() < GLITCH_CHANCE) {
            Wallet wallet = Wallet.of(seller);
            wallet.deductCreditsClamped(GLITCH_PENALTY);
            return "SUPERCOMPUTER GLITCH — " + GLITCH_PENALTY + " credits deducted! New balance: " +
                    wallet.getCredits() + " credits.";
        }
        return "The Floppy Disk transaction completed without error.";
    }
}