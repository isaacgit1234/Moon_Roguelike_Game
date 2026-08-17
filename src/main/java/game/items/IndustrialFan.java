package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Slime;
import game.capabilities.Depositable;
import game.capabilities.Sellable;
import game.spawning.SpawnService;

/**
 * An industrial fan dropped when a Vent is cut.
 *
 * Can be sold to the SuperComputer for 150 worker credits. Selling triggers
 * a hazard - a Slime spawns on an empty tile adjacent to SuperComputer
 *
 * Can be deposited for 10 company credits. Depositing rewards the worker with
 * 10HP healing from a burst of fresh oxygen from the ventilation override.
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public class IndustrialFan extends AbstractItem implements Depositable, Sellable {

    private static final int SELL_PRICE = 150;
    private static final int DEPOSIT_VALUE = 10;
    private static final int HEAL_AMOUNT = 10;

    /**
     * Constructs an Industrial Fan, registering its weight for inventory capacity enforcement.
     */
    public IndustrialFan() {
        super("Industrial Fan", '@', 5);
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
     * Selling strips the facility cooling system — a Slime spawns on an empty
     * tile adjacent to the Supercomputer.
     *
     * <p>Selling is only reachable while the worker stands on the Supercomputer
     * tile, so the seller's location <em>is</em> the Supercomputer's location.
     * The spawn result is checked: if every adjacent tile is blocked, no Slime
     * appears and the message says so rather than claiming a spawn that never
     * happened.</p>
     *
     * @param seller the actor selling
     * @param map    the current game map
     * @return description of what happened
     */
    @Override
    public String onSell(Actor seller, GameMap map) {
        Location supercomputerLocation = map.locationOf(seller);
        Location spawned = new SpawnService().spawnNear(supercomputerLocation, new Slime());
        if (spawned == null) {
            return "Stripping the cooling system triggers a hazard, but no adjacent space "
                    + "remains for a Slime to spawn.";
        }
        return "Stripping the cooling system triggers a hazard! A Slime spawns adjacent to the "
                + "Supercomputer at (" + spawned.x() + ", " + spawned.y() + ").";
    }

    /** @return {@value #DEPOSIT_VALUE} credits */
    @Override
    public int getDepositValue() {
        return DEPOSIT_VALUE;
    }

    /**
     * Depositing rewards the worker with a burst of fresh oxygen - heals 10 HP
     *
     * @param actor the actor depositing
     * @param map the current game map
     * @return description of what happened
     */
    @Override
    public String onDeposit(Actor actor, GameMap map) {
        actor.heal(HEAL_AMOUNT);
        return "Fresh oxygen bursts from the ventilation override! " +
                actor + " is healed for " + HEAL_AMOUNT + " HP.";
    }
}
