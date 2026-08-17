package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.statistics.StatisticOperations;

import game.actions.ConsumeAction;
import game.capabilities.Consumable;
import game.capabilities.Purchasable;
import game.economy.Wallet;

/**
 * A FirstAidKit weighing 25 units.
 *
 * <p><b>Consumption:</b> Permanently increases max HP by 1 and restores to full.
 * Requires a 20-turn cooldown between uses (cooldown pauses when on the ground).</p>
 *
 * <p><b>Purchase:</b> Costs 1,000 credits. Attempting to buy without sufficient
 * funds deeply upsets the Supercomputer — the buyer is killed on the spot.</p>
 *
 * @author Yong Leng Foong
 * @version 1.1
 */
public class FirstAidKit extends AbstractItem implements Consumable, Purchasable {

    private static final int COOLDOWN = 20;
    private static final int PURCHASE_PRICE = 1000;

    private int turnsUntilReady = 0;

    /**
     * Constructs a FirstAidKit, registering its weight.
     */
    public FirstAidKit() {
        super("FirstAidKit", '+', 25);
    }

    // ── Consumable ────────────────────────────────────────────────────────────

    /**
     * Permanently increases actor's max HP by 1 and restores to full health.
     * Starts the 20-turn cooldown.
     *
     * @param actor the actor using the kit
     * @return description of the result
     */
    @Override
    public String consume(Actor actor) {
        actor.modifyStatisticMaximum(ActorStatistics.HEALTH, StatisticOperations.INCREASE, 1);
        int deficit = actor.getMaximumStatistic(ActorStatistics.HEALTH) - actor.getStatistic(ActorStatistics.HEALTH);
        actor.heal(deficit);
        turnsUntilReady = COOLDOWN;
        return actor + " uses the FirstAidKit. Max HP +1 and HP fully restored!";
    }

    /** @return true if the cooldown has elapsed */
    @Override
    public boolean canConsume() {
        return turnsUntilReady == 0;
    }

    /**
     * Engine-facing override — delegates to .
     *
     * @param owner the actor carrying this item
     * @param map   the current game map
     * @return list of allowable actions while carried
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        return allowableActions(owner);
    }

    /**
     * Exposes a {@link ConsumeAction} when on the ground.
     * Eliminates instanceof checks in behaviours (OCP, polymorphism).
     *
     * @param location the location of the ground on which the item lies
     * @return list of allowable actions on the ground
     */
    @Override
    public ActionList allowableActions(Location location) {
        ActionList actions = new ActionList();
        if (canConsume()) {
            actions.add(new ConsumeAction(this, this));
        }
        return actions;
    }

    /**
     * @param actor the actor using FirstAidKit
     * @return menu description string
     */
    @Override
    public String getMenuDescription(Actor actor) {
        return actor + " uses the FirstAidKit.";
    }

    /**
     * Ticks cooldown while carried.
     *
     * @param currentLocation the location of the carrying actor
     * @param actor           the actor carrying this item
     */
    @Override
    public void tick(Location currentLocation, Actor actor) {
        turnsUntilReady = Math.max(0, turnsUntilReady - 1);
    }

    /**
     * Cooldown deliberately pauses when on the ground.
     *
     * @param currentLocation the location of the ground
     */
    @Override
    public void tick(Location currentLocation) {
        // cooldown freezes when not carried
    }

    // ── Purchasable ───────────────────────────────────────────────────────────

    /** @return {@value #PURCHASE_PRICE} credits */
    @Override
    public int getPurchasePrice() {
        return PURCHASE_PRICE;
    }

    /**
     * Adds this FirstAidKit to buyer's inventory.
     *
     * @param buyer the actor buying
     * @param map   the current game map
     * @return description of the outcome
     */
    @Override
    public String onPurchase(Actor buyer, GameMap map) {
        buyer.getInventory().add(new FirstAidKit());
        return buyer + " purchased a FirstAidKit. Remaining balance: " + Wallet.of(buyer).getCredits() + " credits.";
    }

    /**
     * Buyer is insufficient credit.
     * Supercomputer is deeply upset — the buyer is killed instantly.
     *
     * @param buyer the actor buying
     * @param map   the current game map
     * @return description of the outcome
     */
    @Override
    public String onCannotAfford(Actor buyer, GameMap map) {
        buyer.hurt(buyer.getStatistic(ActorStatistics.HEALTH));
        return "The Supercomputer is FURIOUS. " + buyer +
                " cannot afford a FirstAidKit and is terminated on the spot!";
    }
}