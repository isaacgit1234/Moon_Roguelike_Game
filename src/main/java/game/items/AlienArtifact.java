package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Depositable;
import game.capabilities.Sellable;
import game.status.Poisoned;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * An alien artifact dropped when an AlienCube is cut
 *
 * Can be sold to the SuperComputer for 200 worker credits.
 * 50% chance of poisoning the worker due to unstable handling.
 *
 * Can be deposited for 100 company credits.
 * Worker is immediately teleported to a random valid location on the
 * same map to get them back to work faster.
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public class AlienArtifact extends AbstractItem implements Sellable, Depositable {

    private static final int SELL_PRICE = 200;
    private static final int DEPOSIT_VALUE = 100;
    private static final double POISON_CHANCE = 0.50;
    private static final int POISON_DAMAGE = 1;
    private static final int POISON_DURATION = 5;

    private final Random random = new Random();

    /**
     * Constructs an Alien artifact, registering its weight for inventory capacity enforcement.
     */
    public AlienArtifact() {
        super("Alien Artifact", '?', 1);
    }

    /**
     * @param seller the actor selling
     * @return fixed sell price of {@value #SELL_PRICE} credit
     */
    @Override
    public int getSellPrice(Actor seller) {
        return SELL_PRICE;
    }

    /**
     * A 50% chance the worker is poisoned from handling the unstable artifact.
     *
     * @param seller the actor selling
     * @param map the current game map
     * @return description of what happened
     */
    @Override
    public String onSell(Actor seller, GameMap map) {
        if (random.nextDouble() < POISON_CHANCE) {
            seller.addStatus(new Poisoned(POISON_DAMAGE, POISON_DURATION));
            return "The unstable artifact poisons " + seller + " during handling!";
        }
        return seller + " carefully sells the Alien Artifact without incident.";
    }

    /** @return {@value #DEPOSIT_VALUE} credits */
    @Override
    public int getDepositValue() {
        return DEPOSIT_VALUE;
    }

    /**
     * Teleports the worker to a random valid (enterable, unoccupied) tile on the
     * same map. If no such tile exists, the deposit still completes but no
     * teleport occurs.
     *
     * @param actor the actor depositing
     * @param map   the current game map
     * @return description of what happened
     */
    @Override
    public String onDeposit(Actor actor, GameMap map) {
        List<Location> validLocations = new ArrayList<>();
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                if (loc.getGround().canActorEnter(actor) && !loc.containsAnActor()) {
                    validLocations.add(loc);
                }
            }
        }
        if (validLocations.isEmpty()) {
            return actor + " deposited the Alien Artifact, but no valid location was found to teleport to.";
        }
        Location destination = validLocations.get(random.nextInt(validLocations.size()));
        map.moveActor(actor, destination);
        return "The company teleports " + actor + " back to work at ("
                + destination.x() + ", " + destination.y() + ")!";
    }
}
