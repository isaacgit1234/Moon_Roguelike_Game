package game.actors.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import game.actors.Parasite;
import game.actors.VoidStalker;
import game.behaviours.ChaseBehaviour;
import game.capabilities.GameAbilities;
import game.ground.Fire;
import game.spawning.SpawnService;

import java.util.ArrayList;

/**
 * The VoidStalker's pursuit state. Chases the nearest worker and forces
 * them to drop one random item per turn.
 *
 * <p>Transitions:</p>
 * <ul>
 *   <li>→ FrenzyState: 2+ workers adjacent
 *       Exit effect: sets all adjacent floor tiles on Fire</li>
 *   <li>→ IdleState:   no workers adjacent
 *       Exit effect: drops all stalker inventory onto current tile</li>
 * </ul>
 *
 * <p>onEnter effect: spawns a Parasite on each adjacent empty tile.</p>
 *
 * @author Alia Divya Anthony
 * @author Yong Leng Foong
 * @version 2.0
 */
public class HuntingState implements CreatureState {

    private static final int FIRE_LIFETIME = 2;
    private final ChaseBehaviour chaseBehaviour = new ChaseBehaviour(GameAbilities.IS_WORKER);
    private final SpawnService spawnService = new SpawnService();

    @Override
    public StateType getStateType() {
        return StateType.HUNTING;
    }

    @Override
    public Action performAction(VoidStalker stalker, Location location, GameMap map) {
        return chaseBehaviour.operate(stalker, location);
    }

    @Override
    public CreatureState nextState(VoidStalker stalker, Location location, GameMap map) {
        int workers = stalker.countAdjacentWorkers(location);

        if (workers >= 2) {
            return new FrenzyState();
        }

        if (workers == 0) {
            return new IdleState();
        }

        return this;
    }

    /**
     * Hunting → Frenzy: sets all adjacent floor tiles on Fire.
     * Hunting → Idle:   drops all stalker inventory onto current tile.
     */
    @Override
    public void onExit(VoidStalker stalker, Location location, GameMap map, CreatureState nextState) {
        StateType destination = nextState.getStateType();

        if (destination == StateType.FRENZY) {
            for (Exit exit : location.getExits()) {
                Location adj = exit.getDestination();
                if (adj.getGround().hasAbility(GameAbilities.IS_FLOOR)) {
                    adj.setGround(new Fire(adj.getGround(), FIRE_LIFETIME));
                }
            }
        } else if (destination == StateType.IDLE) {
            for (Item item : new ArrayList<>(stalker.getInventory().getItems())) {
                stalker.getInventory().remove(item);
                location.addItem(item);
            }
        }
    }

    /**
     * Unconditional arrival effect: spawns a Parasite on each adjacent empty tile
     * via SpawnService to correctly trigger spawn reactions.
     */
    @Override
    public void onEnter(VoidStalker stalker, Location location, GameMap map) {
        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();
            if (!adj.containsAnActor() && adj.getGround().canActorEnter(stalker)) {
                spawnService.spawnNear(location, new Parasite());
            }
        }
    }
}