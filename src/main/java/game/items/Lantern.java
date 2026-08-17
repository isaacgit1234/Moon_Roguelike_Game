package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import game.behaviours.InfectionDrainBehaviour;
import game.behaviours.OilLeakBehaviour;
import game.behaviours.OilSellEffectBehaviour;
import game.capabilities.GameAbilities;
import game.capabilities.Infectable;
import game.capabilities.Sellable;

/**
 * An unstable light source weighing 7 units.
 *
 * <p><b>Design decisions:</b></p>
 * <ul>
 *   <li><b>SRP:</b> {@code Lantern} has one responsibility — coordinating
 *       its item-level contracts. Every side-effect is delegated to a
 *       dedicated {@link edu.monash.fit2099.engine.behaviours.Behaviour}.</li>
 *   <li><b>OCP:</b> New tick or sell effects require only a new
 *       {@code Behaviour} — {@code Lantern} never changes.</li>
 *   <li><b>DI:</b> {@link InfectionDrainBehaviour} receives
 *       {@link OilLeakBehaviour} via constructor injection, sharing the
 *       same oil supply without either behaviour owning the other.</li>
 *   <li><b>No {@code Display}:</b> Output is returned as strings only.</li>
 * </ul>
 *
 * @author Yong Leng Foong
 * @author Kumali Wickremasinghe
 * @version 5.0
 */
public class Lantern extends AbstractItem implements Sellable, Infectable {

    private static final int CREDITS_PER_OIL = 5;

    private final OilLeakBehaviour        leakBehaviour;
    private final InfectionDrainBehaviour drainBehaviour;

    public Lantern() {
        super("Lantern", '&', 7);
        this.enableAbility(GameAbilities.IS_INFECTABLE);

        this.leakBehaviour  = new OilLeakBehaviour();
        this.drainBehaviour = new InfectionDrainBehaviour(leakBehaviour);
    }

    // ── Sellable ──────────────────────────────────────────────────────────────

    @Override
    public int getSellPrice(Actor seller) {
        return leakBehaviour.getRemaining() * CREDITS_PER_OIL;
    }

    @Override
    public String onSell(Actor seller, GameMap map) {
        return new OilSellEffectBehaviour(seller, map).operate(this, map.locationOf(seller));
    }

    // ── Item tick ─────────────────────────────────────────────────────────────

    @Override
    public void tick(Location currentLocation) {
        super.tick(currentLocation);
        drainBehaviour.operate(this, currentLocation);
    }

    @Override
    public void tick(Location currentLocation, Actor actor) {
        drainBehaviour.operate(this, currentLocation);
        leakBehaviour.operate(this, currentLocation);
    }

    // ── Infectable ────────────────────────────────────────────────────────────

    @Override
    public String infect(Actor source, Location location, GameMap map) {
        drainBehaviour.activate();
        return source + " infects the Lantern. The Lantern will lose 1 oil every turn.";
    }
}