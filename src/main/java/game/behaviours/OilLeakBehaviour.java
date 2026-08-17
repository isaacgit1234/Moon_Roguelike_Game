package game.behaviours;

import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;

import game.ground.Fire;

import java.util.Random;

/**
 * Manages the oil state of a {@link game.items.Lantern} and applies the
 * passive leak behaviour each turn while the Lantern is carried.
 *
 * <p>Oil state and leak behaviour are bound here because they are
 * inseparable concerns — the leak <em>is</em> what consumes oil, and
 * oil only exists to be consumed by the leak. Splitting them would
 * introduce unnecessary indirection with no design gain.</p>
 *
 * <p><b>SRP:</b> One responsibility — track oil and decide whether a
 * leak occurs each turn.</p>
 *
 * <p><b>OCP:</b> Leak probability or oil capacity can change here
 * without touching {@link game.items.Lantern}.</p>
 *
 * @author Yong Leng Foong
 * @version 2.0
 */
public class OilLeakBehaviour implements Behaviour<Item, String> {

    private static final double LEAK_CHANCE  = 0.05;
    private static final int    INITIAL_OIL  = 10;

    private final Random random = new Random();
    private int oil;

    public OilLeakBehaviour() {
        this.oil = INITIAL_OIL;
    }

    // ── Oil state ─────────────────────────────────────────────────────────────

    /** @return how much oil remains */
    public int getRemaining() {
        return oil;
    }

    /** @return true if any oil remains */
    public boolean hasOil() {
        return oil > 0;
    }

    /**
     * Drains one unit of oil.
     *
     * @param reason description of why oil was drained
     * @return a drain description, or {@code null} if already empty
     */
    public String drainOne(String reason) {
        if (oil <= 0) {
            return null;
        }
        oil--;
        return reason + " (" + oil + " oil remaining)";
    }

    // ── Behaviour ─────────────────────────────────────────────────────────────

    /**
     * Rolls for a passive leak each turn. On success, drains one oil unit
     * and spawns {@link Fire} on the carrier's tile.
     *
     * @param item     the lantern (kept for interface contract)
     * @param location the carrier's current location
     * @return a description of the leak, or {@code null} if no leak occurred
     */
    @Override
    public String operate(Item item, Location location) {
        if (!hasOil()) {
            return null;
        }
        if (random.nextDouble() < LEAK_CHANCE) {
            String drain = drainOne("Lantern leaks");
            location.setGround(new Fire(location.getGround()));
            return drain + " — Fire breaks out!";
        }
        return null;
    }
}