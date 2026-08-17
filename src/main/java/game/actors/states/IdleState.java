package game.actors.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import game.actors.VoidStalker;
import game.capabilities.GameAbilities;
import game.ground.ToxicWaste;
import game.items.Flask;

import java.util.ArrayList;

/**
 * The VoidStalker's default resting state. Does nothing each turn.
 *
 * <p>Transitions:</p>
 * <ul>
 *   <li>→ HuntingState:   1 worker adjacent
 *       Exit effect: spawns ToxicWaste on all adjacent floor tiles</li>
 *   <li>→ FrenzyState:    2+ workers adjacent
 *       Exit effect: spawns ToxicWaste on all adjacent floor tiles</li>
 *   <li>→ DefensiveState: HP below 50%
 *       Exit effect: removes all items from adjacent tiles to current tile</li>
 * </ul>
 *
 * <p>onEnter effect: spawns a Flask on every adjacent empty floor tile.</p>
 *
 * @author Alia Divya Anthony
 * @author Yong Leng Foong
 * @version 2.0
 */
public class IdleState implements CreatureState {

    @Override
    public StateType getStateType() {
        return StateType.IDLE;
    }

    @Override
    public Action performAction(VoidStalker stalker, Location location, GameMap map) {
        return new DoNothingAction();
    }

    @Override
    public CreatureState nextState(VoidStalker stalker, Location location, GameMap map) {
        int workers = stalker.countAdjacentWorkers(location);

        if (stalker.getCurrentHp() < stalker.getLowHpThreshold()) {
            return new DefensiveState();
        }

        if (workers >= 2) {
            return new FrenzyState();
        }

        if (workers == 1) {
            return new HuntingState();
        }

        return this;
    }

    /**
     * Idle → Hunting/Frenzy: corrupts adjacent floor tiles with ToxicWaste.
     * Idle → Defensive:      clears all items from adjacent tiles to current tile.
     */
    @Override
    public void onExit(VoidStalker stalker, Location location, GameMap map, CreatureState nextState) {
        StateType destination = nextState.getStateType();

        if (destination == StateType.HUNTING || destination == StateType.FRENZY) {
            for (Exit exit : location.getExits()) {
                Location adj = exit.getDestination();
                if (adj.getGround().hasAbility(GameAbilities.IS_FLOOR)) {
                    adj.setGround(new ToxicWaste());
                }
            }
        } else if (destination == StateType.DEFENSIVE) {
            for (Exit exit : location.getExits()) {
                Location adj = exit.getDestination();
                for (Item item : new ArrayList<>(adj.getItems())) {
                    adj.removeItem(item);
                    location.addItem(item);
                }
            }
        }
    }

    /**
     * Unconditional arrival effect: spawns a Flask on each adjacent empty floor tile.
     * Represents the stalker releasing stored energy as it settles into rest.
     */
    @Override
    public void onEnter(VoidStalker stalker, Location location, GameMap map) {
        for (Exit exit : location.getExits()) {
            Location adj = exit.getDestination();
            if (!adj.containsAnActor() && adj.getGround().hasAbility(GameAbilities.IS_FLOOR)) {
                adj.addItem(new Flask());
            }
        }
    }
}