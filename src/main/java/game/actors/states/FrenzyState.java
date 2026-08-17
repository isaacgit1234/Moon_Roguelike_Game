package game.actors.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import game.actions.MultiAttackAction;
import game.actors.Parasite;
import game.actors.VoidStalker;
import game.capabilities.GameAbilities;
import game.ground.Fire;
import game.spawning.SpawnService;
import game.status.Burned;

/**
 * The VoidStalker's most dangerous state. Attacks ALL adjacent workers
 * simultaneously each turn via {@link MultiAttackAction}.
 *
 * <p>Transitions:</p>
 * <ul>
 *   <li>→ HuntingState:   1 worker adjacent
 *       Exit effect: spawns a Parasite on each adjacent empty tile</li>
 *   <li>→ DefensiveState: HP below 30%
 *       Exit effect: applies Burned status to all adjacent actors</li>
 * </ul>
 *
 * <p>onEnter effect: sets all adjacent floor tiles on Fire.</p>
 *
 * @author Alia Divya Anthony
 * @author Yong Leng Foong
 * @version 2.0
 */
public class FrenzyState implements CreatureState {

    private static final int ATTACK_DAMAGE = 3;
    private static final int HIT_CHANCE = 100;
    private static final int BURN_DAMAGE = 1;
    private static final int BURN_DURATION = 3;
    private static final int FIRE_LIFETIME = 2;

    private final SpawnService spawnService = new SpawnService();

    @Override
    public StateType getStateType() {
        return StateType.FRENZY;
    }

    @Override
    public Action performAction(VoidStalker stalker, Location location, GameMap map) {
        return new MultiAttackAction(GameAbilities.IS_WORKER, ATTACK_DAMAGE, HIT_CHANCE);
    }

    @Override
    public CreatureState nextState(VoidStalker stalker, Location location, GameMap map) {
        int workers = stalker.countAdjacentWorkers(location);

        if (stalker.getCurrentHp() < stalker.getCriticalHpThreshold()) {
            return new DefensiveState();
        }

        if (workers == 1) {
            return new HuntingState();
        }

        return this;
    }

    /**
     * Frenzy → Hunting:   spawns a Parasite on each adjacent empty tile via SpawnService.
     * Frenzy → Defensive: applies Burned status to all adjacent actors.
     */
    @Override
    public void onExit(VoidStalker stalker, Location location, GameMap map, CreatureState nextState) {
        StateType destination = nextState.getStateType();

        if (destination == StateType.HUNTING) {
            for (Exit exit : location.getExits()) {
                Location adj = exit.getDestination();
                if (!adj.containsAnActor() && adj.getGround().canActorEnter(stalker)) {
                    spawnService.spawnNear(location, new Parasite());
                }
            }
        } else if (destination == StateType.DEFENSIVE) {
            for (Exit exit : location.getExits()) {
                Actor target = exit.getDestination().getActor();
                if (target != null) {
                    target.addStatus(new Burned(BURN_DAMAGE, BURN_DURATION));
                }
            }
        }
    }

    /**
     * Unconditional arrival effect: sets all adjacent floor tiles on Fire.
     * Represents explosive aggression as the stalker enters full frenzy.
     */
    @Override
    public void onEnter(VoidStalker stalker, Location location, GameMap map) {
        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();
            if (adj.getGround().hasAbility(GameAbilities.IS_FLOOR)) {
                adj.setGround(new Fire(adj.getGround(), FIRE_LIFETIME));
            }
        }
    }
}