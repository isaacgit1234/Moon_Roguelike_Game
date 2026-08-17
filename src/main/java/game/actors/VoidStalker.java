package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import game.actors.states.CreatureState;
import game.actors.states.IdleState;
import game.capabilities.GameAbilities;
import game.inventories.BasicInventory;

/**
 * The Void Stalker — a biomechanical predator found on 20-overflow.
 * Mutated by prolonged exposure to toxic waste, it cycles through four
 * distinct behavioural states depending on its environment and health.
 *
 * <p><b>SRP:</b> VoidStalker delegates all state logic to CreatureState implementations.</p>
 * <p><b>OCP:</b> New states can be added by implementing CreatureState without
 *     modifying this class.</p>
 * <p><b>DIP:</b> VoidStalker depends on the CreatureState interface, not concrete states.</p>
 *
 * @author Alia Divya Anthony
 * @author Yong Leng Foong
 * @version 2.0
 */
public class VoidStalker extends BehaviouralActor {

    private static final int HIT_POINTS = 40;
    private static final int LOW_HP_THRESHOLD = 20;
    private static final int CRITICAL_HP_THRESHOLD = 12;

    private CreatureState currentState;

    /**
     * Constructs a VoidStalker starting in IdleState.
     */
    public VoidStalker() {
        super("Void Stalker", 'Ψ', HIT_POINTS, new BasicInventory());
        this.currentState = new IdleState();
        this.enableAbility(GameAbilities.IS_CREATURE);
    }

    /**
     * Returns the current HP of the stalker.
     *
     * @return current HP value
     */
    public int getCurrentHp() {
        return this.getStatistic(ActorStatistics.HEALTH);
    }

    /**
     * Returns the HP threshold below which the stalker enters DefensiveState.
     *
     * @return low HP threshold (50% of max)
     */
    public int getLowHpThreshold() {
        return LOW_HP_THRESHOLD;
    }

    /**
     * Returns the HP threshold below which the stalker enters DefensiveState
     * from FrenzyState.
     *
     * @return critical HP threshold (30% of max)
     */
    public int getCriticalHpThreshold() {
        return CRITICAL_HP_THRESHOLD;
    }

    /**
     * Counts the number of workers in immediately adjacent tiles.
     *
     * @param location the stalker's current location
     * @return number of adjacent workers
     */
    public int countAdjacentWorkers(Location location) {
        int count = 0;
        for (Exit exit : location.getExits()) {
            Actor actor = exit.getDestination().getActor();
            if (actor != null && actor.hasAbility(GameAbilities.IS_WORKER)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Each turn:
     * 1. Evaluates whether a state transition should occur
     * 2. If transitioning, calls onExit on the leaving state (transition effect)
     * 3. Calls onEnter on the arriving state (unconditional arrival effect)
     * 4. Performs the current state's turn action
     *
     * @param actions    available actions from the engine
     * @param lastAction the last action performed
     * @param map        the current game map
     * @param display    the display for output
     * @return the action to perform this turn
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        Location location = map.locationOf(this);

        CreatureState nextState = currentState.nextState(this, location, map);

        if (nextState != currentState) {
            currentState.onExit(this, location, map, nextState);
            currentState = nextState;
            currentState.onEnter(this, location, map);
            display.println("Void Stalker transitions to " +
                    currentState.getClass().getSimpleName() + "!");
        }

        Action action = currentState.performAction(this, location, map);
        return action != null ? action : new DoNothingAction();
    }
}