package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import game.ground.Fire;
import game.capabilities.Sellable;

import java.util.Random;

/**
 * A massive piece of archaic junk weighing 30 units.
 *
 * <p><b>Selling:</b> Sells for 25 credits. Offloading the weight immediately
 * heals the seller for 5 HP. There is a 20% chance the archaic hardware shorts
 * out the terminal, dealing 2 damage to the seller and spawning Fire on all
 * surrounding tiles.</p>
 *
 * @author Yong Leng Foong
 * @version 1.1
 */
public class CRTMonitor extends AbstractItem implements Sellable{

    private static final int SELL_PRICE = 25;
    private static final int RELIEF_HEAL = 5;
    private static final double SHORT_CHANCE = 0.20;
    private static final int SHORT_DAMAGE = 2;

    private final Random random = new Random();

    /**
     * Constructs a CRT Monitor, registering its weight for inventory capacity enforcement.
     */
    public CRTMonitor(){
        super("CRT Monitor", '◙', 30);
    }

    // ── Sellable ──────────────────────────────────────────────────────────────

    /** @return fixed price of {@value #SELL_PRICE} credits */
    @Override
    public int getSellPrice(Actor seller) {
        return SELL_PRICE;
    }

    /**
     * Always heals the seller {@value #RELIEF_HEAL} HP (back relief).
     * 20% chance the hardware shorts: deals {@value #SHORT_DAMAGE} damage
     * and spawns Fire on all surrounding tiles.
     *
     * @param seller the actor selling the CRT Monitor
     * @param map    the current game map
     * @return description of side-effects
     */
    @Override
    public String onSell(Actor seller, GameMap map) {
        seller.heal(RELIEF_HEAL);
        StringBuilder result = new StringBuilder(seller + " feels immediate relief — +" + RELIEF_HEAL + " HP. ");

        if (random.nextDouble() < SHORT_CHANCE) {
            seller.hurt(SHORT_DAMAGE);
            result.append(seller).append(" is shocked by a short circuit! -").append(SHORT_DAMAGE).append(" HP. ");

            Location sellerLocation = map.locationOf(seller);
            for (var exit : sellerLocation.getExits()) {
                Location neighbour = exit.getDestination();
                if (neighbour.canActorEnter(seller)) {
                    neighbour.setGround(new Fire(neighbour.getGround()));
                }
            }
            result.append("Fire erupts on surrounding tiles!");
        }
        return result.toString();
    }
}
