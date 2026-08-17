package game.ground;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;

import game.actions.ConsumeAction;
import game.capabilities.Consumable;
import game.capabilities.GameAbilities;
import game.status.Poisoned;

/**
 * A small, stationary body of mysterious liquid on the ground.
 * In a standard video game, this would just be water. On a deprecated moon
 * in the Eclipse Nebula, it could be anything from spilled engine coolant to
 * highly corrosive alien saliva. Step in it at your own risk.
 *
 * Without sterilisation - the water poisons them (1 damage per turn for 3 turns)
 * With sterilisation — the water heals them for 1 HP.
 *
 * Puddles are an infinite resource — they do not deplete.
 *
 * @author Yong Leng Foong
 * @version 1.1
 */
public class Puddle extends Ground implements Consumable {

    private static final int HEAL_AMOUNT = 1;
    private static final int POISON_DAMAGE = 1;
    private static final int POISON_DURATION = 3;

    /**
     * Constructs a Puddle.
     */
    public Puddle() {
        super('~', "Puddle");
        enableAbility(GameAbilities.IS_PUDDLE);
    }

    /**
     * Offers a drink action when the actor is standing directly on the puddle.
     * The engine calls this with an empty direction string for the tile the
     * actor currently occupies, and a non-empty direction for adjacent tiles.
     *
     * @param actor     the actor querying for available actions
     * @param location  this Puddle's location
     * @param direction empty when the actor stands on this tile; non-empty otherwise
     * @return a list containing the drink action if applicable, otherwise empty
     */
    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();
        if (direction.isEmpty() && canConsume()) {
            actions.add(new ConsumeAction(this));
        }
        return actions;
    }

    /**
     * Drinks from the puddle. Effect depends on whether the actor carries a
     * SterilisationBox (granting {@link GameAbilities#STERILISED}).
     *
     * @param actor the actor drinking
     * @return a description of what happened
     */
    @Override
    public String consume(Actor actor) {
        if (actor.hasAbility(GameAbilities.STERILISED)) {
            actor.heal(HEAL_AMOUNT);
            return actor + " drinks from a sterilised Puddle, healing " + HEAL_AMOUNT + " HP.";
        }
        actor.addStatus(new Poisoned(POISON_DAMAGE, POISON_DURATION));
        return actor + " drinks from a toxic Puddle and is Poisoned for " + POISON_DURATION + " turns.";
    }

    /**
     * @return true — a puddle is always drinkable
     */
    @Override
    public boolean canConsume() {
        return true;
    }

    /**
     * Returns the menu description for drinking from Puddle.
     *
     * @param actor the actor drinking from Puddle
     * @return menu description string
     */
    @Override
    public String getMenuDescription(Actor actor) {
        return actor + " drinks from the Puddle.";
    }
}
