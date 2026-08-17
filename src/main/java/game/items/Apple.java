package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

import edu.monash.fit2099.engine.positions.Location;
import game.actions.ConsumeAction;
import game.capabilities.Consumable;
import game.capabilities.GameAbilities;
import game.capabilities.Sellable;
import game.status.Poisoned;

/**
 * A spoiled fruit weighing 1 unit. Toxic unless the consumer carries a
 * {@link SterilisationBox}.
 *
 * <p><b>Consumption:</b> Without sterilisation — inflicts {@link Poisoned}
 * (1 damage/turn for 5 turns). With sterilisation — heals 3 HP.</p>
 *
 * <p><b>Selling:</b> Sells for 1 credit. Unless the seller carries a
 * {@link SterilisationBox}, they are immediately poisoned for 2 turns
 * (2 damage/turn).</p>
 *
 * @author Yong Leng Foong
 * @version 1.1
 */
public class Apple extends AbstractItem implements Consumable, Sellable {

    private static final int HEAL_AMOUNT = 3;
    private static final int CONSUME_POISON_DAMAGE = 1;
    private static final int CONSUME_POISON_DURATION = 5;
    private static final int SELL_PRICE = 1;
    private static final int SELL_POISON_DAMAGE = 2;
    private static final int SELL_POISON_DURATION = 2;

    /**
     * Constructs an Apple, registering its weight for inventory capacity enforcement.
     */
    public Apple() {
        super("Apple", 'ó', 1);
    }

    // ── Consumable ────────────────────────────────────────────────────────────

    /**
     * Eats the Apple. Heals if sterilised, poisons otherwise.
     * Removes itself from inventory after use.
     *
     * @param actor the actor consuming the Apple
     * @return description of the effect
     */
    @Override
    public String consume(Actor actor) {
        String result;
        if (actor.hasAbility(GameAbilities.STERILISED)) {
            actor.heal(HEAL_AMOUNT);
            result = actor + " eats a sterilised Apple, healing " + HEAL_AMOUNT + " HP.";
        } else {
            actor.addStatus(new Poisoned(CONSUME_POISON_DAMAGE, CONSUME_POISON_DURATION));
            result = actor + " eats a spoiled Apple and is poisoned for " + CONSUME_POISON_DURATION + " turns.";
        }
        actor.getInventory().remove(this);
        return result;
    }

    /** @return True — an Apple can always be consumed while it exists */
    @Override
    public boolean canConsume() {
        return true;
    }

    /**
     * Exposes a {@link ConsumeAction} when carried by an actor.
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        if (canConsume()) {
            actions.add(new ConsumeAction(this, this));
        }
        return actions;
    }

    /**
     * Exposes a {@link ConsumeAction} when on the ground.
     * Eliminates instanceof checks in behaviours (OCP, polymorphism).
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
     * Returns the menu description for consuming Apple.
     *
     * @param actor the actor consuming Apple
     * @return menu description string
     */
    @Override
    public String getMenuDescription(Actor actor) {
        return actor + " eats an Apple.";
    }

    // ── Sellable ──────────────────────────────────────────────────────────────

    /**
     * @param seller the actor selling
     * @return fixed sell price of {@value #SELL_PRICE} credit
     */
    @Override
    public int getSellPrice(Actor seller) {
        return SELL_PRICE;
    }

    /**
     * Poisons the seller for 2 turns (2 damage/turn) unless they carry a
     * {@link SterilisationBox}.
     *
     * @param seller the actor selling the Apple
     * @param map    the current game map (unused)
     * @return description of side-effect
     */
    @Override
    public String onSell(Actor seller, GameMap map) {
        if (!seller.hasAbility(GameAbilities.STERILISED)) {
            seller.addStatus(new Poisoned(SELL_POISON_DAMAGE, SELL_POISON_DURATION));
            return seller + " is poisoned by the Apple's toxic residue! (" +
                    SELL_POISON_DURATION + " turns, " + SELL_POISON_DAMAGE + " dmg/turn)";
        }
        return seller + "'s Sterilisation Box neutralised the Apple's toxins.";
    }
}