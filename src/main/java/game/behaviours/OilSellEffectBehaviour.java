package game.behaviours;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import game.capabilities.GameAbilities;
import game.ground.Fire;
import game.status.Burned;

import java.util.Random;

/**
 * Encapsulates the two probabilistic side-effects that fire when a
 * {@link game.items.Lantern} is sold:
 * <ol>
 *   <li>50% chance: seller receives a {@link Burned} status</li>
 *   <li>25% chance: {@link Fire} spawns on all adjacent floor tiles</li>
 * </ol>
 *
 * <p><b>SRP:</b> {@link game.items.Lantern#onSell} no longer owns burn logic
 * or fire-spread logic. This class owns both sell-time side-effects and
 * nothing else.</p>
 *
 * <p><b>OCP:</b> Adding a third sell-time effect requires only modifying or
 * subclassing this behaviour, not touching {@link game.items.Lantern}.</p>
 *
 * <p>Analogy: think of this as the hazmat protocol triggered when a volatile
 * item changes hands. The item itself doesn't know what happens at the
 * transaction counter — this protocol does.</p>
 *
 * @author Yong Leng Foong
 * @version 1.1
 */
public class OilSellEffectBehaviour implements Behaviour<Item, String> {

    private static final double BURN_SELLER_CHANCE = 0.50;
    private static final double FIRE_SPAWN_CHANCE  = 0.25;
    private static final int    SELL_BURN_DAMAGE   = 2;
    private static final int    SELL_BURN_DURATION = 3;

    private final Random  random;
    private final Actor   seller;
    private final GameMap map;

    /**
     * @param seller the actor selling the Lantern
     * @param map    the current game map
     */
    public OilSellEffectBehaviour(Actor seller, GameMap map) {
        this.random = new Random();
        this.seller = seller;
        this.map    = map;
    }

    /**
     * Rolls and applies both independent sell-time effects.
     *
     * @param item     the lantern being sold (unused; kept for interface contract)
     * @param location unused; kept for interface contract
     * @return a description of which effects triggered
     */
    @Override
    public String operate(Item item, Location location) {
        StringBuilder result = new StringBuilder();

        if (random.nextDouble() < BURN_SELLER_CHANCE) {
            applyBurnToSeller();
            result.append(seller).append(" is burned by the volatile lantern! ");
        }

        if (random.nextDouble() < FIRE_SPAWN_CHANCE) {
            spawnFireAroundSeller();
            result.append("The lantern explodes outward — Fire engulfs surrounding tiles!");
        }

        return result.isEmpty() ? "The lantern was sold without incident." : result.toString();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void applyBurnToSeller() {
        Burned.applyOrExtend(seller, SELL_BURN_DAMAGE, SELL_BURN_DURATION);
    }

    private void spawnFireAroundSeller() {
        Location sellerLocation = map.locationOf(seller);
        for (var exit : sellerLocation.getExits()) {
            Location neighbour = exit.getDestination();
            if (neighbour.getGround().hasAbility(GameAbilities.IS_FLOOR)) {
                neighbour.setGround(new Fire(neighbour.getGround()));
            }
        }
    }
}