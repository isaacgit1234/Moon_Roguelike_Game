package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;

import game.actions.AttackAction;
import game.capabilities.GameAbilities;

/**
 * A behaviour that searches surrounding locations for a target with
 * {@link game.capabilities.GameAbilities#IS_WORKER} and
 * {@link game.actions.AttackAction} if found.
 * Used exclusively by {@link game.actors.Undead}.
 *
 * @author Yong Leng Foong
 * @version 1.0
 */
public class AttackBehaviour implements Behaviour<Actor, Action> {

    private final int damage;
    private final int hitChancePercent;

    /**
     * Constructs an AttackBehaviour with configurable damage and hit chance.
     *
     * @param damage damage dealt on a successful hit
     * @param hitChancePercent percentage chance to hit (0-100)
     */
    public AttackBehaviour(int damage, int hitChancePercent) {
        this.damage = damage;
        this.hitChancePercent = hitChancePercent;
    }

    /**
     * Scans adjacent exits for an actor carrying {@link GameAbilities#IS_WORKER}
     * and returns an attack action targeting the first one found.
     *
     * @param actor the actor performing the behaviour
     * @param location the current location of the actor
     * @return an {@link AttackAction} if a worker is adjacent, null otherwise
     */
    @Override
    public Action operate(Actor actor, Location location) {
        for (Exit exit : location.getExits()) {
            Location dest = exit.getDestination();
            Actor target = dest.getActor();
            if (target != null && target.hasAbility(GameAbilities.IS_WORKER)) {
                return new AttackAction(target, exit.getName(), damage, hitChancePercent);
            }
        }
        return null;
    }
}
