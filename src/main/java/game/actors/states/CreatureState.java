package game.actors.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import game.actors.VoidStalker;

/**
 * Interface for all VoidStalker behavioural states.
 *
 * <p>Transition effects are split across two hooks:</p>
 * <ul>
 *   <li>{@link #onExit} — called on the leaving state, receives the destination
 *       so it can apply the correct transition effect.</li>
 *   <li>{@link #onEnter} — called on the entering state for unconditional
 *       arrival effects that don't depend on origin.</li>
 * </ul>
 *
 * <p>{@link #getStateType} exposes an identity token so {@code onExit}
 * can branch on destination type without instanceof checks — upholding OCP
 * and DIP.</p>
 *
 * @author Alia Divya Anthony
 * @author Yong Leng Foong
 * @version 2.0
 */
public interface CreatureState {

    /**
     * Performs this state's turn action.
     *
     * @param stalker  the VoidStalker acting
     * @param location the stalker's current location
     * @param map      the current game map
     * @return the action to execute this turn
     */
    Action performAction(VoidStalker stalker, Location location, GameMap map);

    /**
     * Evaluates transition conditions and returns the next state.
     * Returns {@code this} if no transition should occur.
     *
     * @param stalker  the VoidStalker acting
     * @param location the stalker's current location
     * @param map      the current game map
     * @return the next CreatureState (may be this)
     */
    CreatureState nextState(VoidStalker stalker, Location location, GameMap map);

    /**
     * Called on the leaving state just before the transition occurs.
     * The destination state is provided so the correct transition effect
     * can be applied — different destinations require different effects.
     *
     * @param stalker   the VoidStalker transitioning
     * @param location  the stalker's current location
     * @param map       the current game map
     * @param nextState the state being transitioned into
     */
    void onExit(VoidStalker stalker, Location location, GameMap map, CreatureState nextState);

    /**
     * Called on the entering state after the transition.
     * Use for unconditional arrival effects independent of origin state.
     *
     * @param stalker  the VoidStalker entering this state
     * @param location the stalker's current location
     * @param map      the current game map
     */
    void onEnter(VoidStalker stalker, Location location, GameMap map);

    /**
     * Returns the enum identity of this state.
     * Used by onExit() to branch on destination type without instanceof.
     *
     * @return this state's {@link StateType}
     */
    StateType getStateType();
}