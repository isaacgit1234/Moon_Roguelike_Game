package game.behaviours;

import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Drains one unit of oil per turn when the {@link game.items.Lantern}
 * has been infected by a {@link game.actors.Parasite}.
 *
 * <p>Shares oil state with {@link OilLeakBehaviour} via constructor
 * injection — both behaviours operate on the same oil supply without
 * either owning the other.</p>
 *
 * <p><b>SRP:</b> One responsibility — apply infection drain each turn
 * once activated.</p>
 *
 * @author Yong Leng Foong
 * @version 2.0
 */
public class InfectionDrainBehaviour implements Behaviour<Item, String> {

    private final OilLeakBehaviour oil;
    private boolean active = false;

    /**
     * @param oil the shared oil behaviour; drained each turn when active
     */
    public InfectionDrainBehaviour(OilLeakBehaviour oil) {
        this.oil = oil;
    }

    /** Activates the drain. Called once when the Lantern is infected. */
    public void activate() {
        this.active = true;
    }

    /**
     * Drains one oil unit per turn while active.
     *
     * @param item     the lantern (kept for interface contract)
     * @param location current location (kept for interface contract)
     * @return a drain description, or {@code null} if inactive or oil is gone
     */
    @Override
    public String operate(Item item, Location location) {
        if (!active) {
            return null;
        }
        return oil.drainOne("Infected Lantern loses 1 oil");
    }
}