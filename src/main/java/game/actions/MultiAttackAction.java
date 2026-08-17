// NEW FILE: game/actions/MultiAttackAction.java
package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import game.capabilities.GameAbilities;

import java.util.Random;

/**
 * An action that attacks ALL adjacent actors with a specified ability simultaneously.
 *
 * <p>Used by FrenzyState to express multi-target attacks as a single proper
 * Action returned to the engine, rather than calling execute() as a side-effect
 * inside performAction (which discards result strings and bypasses engine tracking).</p>
 *
 * <p><b>SRP:</b> Handles only multi-target attack execution logic.</p>
 * <p><b>OCP:</b> Target type is defined by ability — new target types need zero changes here.</p>
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public class MultiAttackAction extends Action {

    private final GameAbilities targetAbility;
    private final int damage;
    private final int hitChancePercent;
    private final Random random = new Random();

    /**
     * @param targetAbility   the ability identifying valid targets
     * @param damage          damage per hit
     * @param hitChancePercent hit chance (0–100)
     */
    public MultiAttackAction(GameAbilities targetAbility, int damage, int hitChancePercent) {
        this.targetAbility = targetAbility;
        this.damage = damage;
        this.hitChancePercent = hitChancePercent;
    }

    /**
     * Attacks every adjacent actor matching the target ability.
     * Each target is rolled independently.
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Location location = map.locationOf(actor);
        StringBuilder result = new StringBuilder();

        for (Exit exit : location.getExits()) {
            Actor target = exit.getDestination().getActor();
            if (target != null && target.hasAbility(targetAbility)) {
                if (random.nextInt(100) < hitChancePercent) {
                    target.hurt(damage);
                    result.append(actor).append(" hits ").append(target)
                            .append(" for ").append(damage).append(" damage! ");
                } else {
                    result.append(actor).append(" misses ").append(target).append("! ");
                }
            }
        }

        return result.isEmpty() ? actor + " finds no targets." : result.toString().trim();
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " frenzies — attacks all adjacent workers!";
    }
}