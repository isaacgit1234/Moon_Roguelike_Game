package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

import java.util.Random;

/**
 * An action representing a physical attack against a target actor.
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public class AttackAction extends Action {

    private final Actor target;
    private final String direction;
    private final int damage;
    private final int hitChancePercent;
    private final Random random = new Random();

    /**
     * Constructor for a configurable attack.
     *
     * @param target the actor to attack
     * @param direction the direction string for display
     * @param damage damage dealt on hit
     * @param hitChancePercent percentage chance to hit (0 - 100)
     */
    public AttackAction(Actor target, String direction, int damage, int hitChancePercent) {
        this.target = target;
        this.direction = direction;
        this.damage = damage;
        this.hitChancePercent = hitChancePercent;
    }

    /**
     * Executes the attack by rolling against the hit chance. On a successful hit,
     * the target takes damage via the engine's {@code hurt()} method.
     *
     * @param actor the actor performing the attack
     * @param map the map the actor is on
     * @return a description of whether the attack hit or missed
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (random.nextInt(100) < this.hitChancePercent) {
            target.hurt(damage);
            return actor + " hits " + target + " for " + damage + " damage!";
        }
        return actor + " misses " + target + "!";
    }

    /**
     * Returns the menu description for this attack action.
     *
     * @param actor the actor performing the attack
     * @return the menu description string
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " attacks " + target + " at " + direction;
    }
}
