package game.actors.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import game.actors.VoidStalker;
import game.behaviours.RetreatBehaviour;
import game.capabilities.GameAbilities;
import game.items.Flask;
import game.status.Burned;
import game.status.Poisoned;

/**
 * The VoidStalker's retreat state, triggered by low HP.
 * Flees from workers while leaving ToxicWaste in its wake via RetreatBehaviour.
 *
 * <p>Transitions:</p>
 * <ul>
 *   <li>→ IdleState:   HP above 50% AND no workers nearby
 *       Exit effect: spawns a Flask on every adjacent empty floor tile</li>
 *   <li>→ FrenzyState: HP above 70% AND 2+ workers nearby
 *       Exit effect: poisons all adjacent workers (2 damage/turn for 3 turns)</li>
 * </ul>
 *
 * <p>onEnter effect: applies Burned status to all adjacent actors.</p>
 *
 * @author Alia Divya Anthony
 * @author Yong Leng Foong
 * @version 2.0
 */
public class DefensiveState implements CreatureState {

    private static final int RECOVERY_THRESHOLD = 28;
    private static final int POISON_DAMAGE = 2;
    private static final int POISON_DURATION = 3;
    private static final int BURN_DAMAGE = 1;
    private static final int BURN_DURATION = 3;

    private final RetreatBehaviour retreatBehaviour = new RetreatBehaviour(GameAbilities.IS_WORKER);

    @Override
    public StateType getStateType() {
        return StateType.DEFENSIVE;
    }

    @Override
    public Action performAction(VoidStalker stalker, Location location, GameMap map) {
        return retreatBehaviour.operate(stalker, location);
    }

    @Override
    public CreatureState nextState(VoidStalker stalker, Location location, GameMap map) {
        int workers = stalker.countAdjacentWorkers(location);
        int hp = stalker.getCurrentHp();

        if (hp >= stalker.getLowHpThreshold() && workers == 0) {
            return new IdleState();
        }

        if (hp >= RECOVERY_THRESHOLD && workers >= 2) {
            return new FrenzyState();
        }

        return this;
    }

    /**
     * Defensive → Idle:   spawns a Flask on each adjacent empty floor tile.
     * Defensive → Frenzy: poisons all adjacent workers.
     */
    @Override
    public void onExit(VoidStalker stalker, Location location, GameMap map, CreatureState nextState) {
        StateType destination = nextState.getStateType();

        if (destination == StateType.IDLE) {
            for (Exit exit : location.getExits()) {
                Location adj = exit.getDestination();
                if (!adj.containsAnActor() && adj.getGround().hasAbility(GameAbilities.IS_FLOOR)) {
                    adj.addItem(new Flask());
                }
            }
        } else if (destination == StateType.FRENZY) {
            for (Exit exit : location.getExits()) {
                Actor target = exit.getDestination().getActor();
                if (target != null && target.hasAbility(GameAbilities.IS_WORKER)) {
                    target.addStatus(new Poisoned(POISON_DAMAGE, POISON_DURATION));
                }
            }
        }
    }

    /**
     * Unconditional arrival effect: applies Burned status to all adjacent actors.
     * Represents residual heat radiating outward as the stalker crashes into retreat.
     */
    @Override
    public void onEnter(VoidStalker stalker, Location location, GameMap map) {
        for (Exit exit : location.getExits()) {
            Actor target = exit.getDestination().getActor();
            if (target != null) {
                target.addStatus(new Burned(BURN_DAMAGE, BURN_DURATION));
            }
        }
    }
}