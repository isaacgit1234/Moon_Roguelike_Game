package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.behaviours.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.GameMap;
import game.behaviours.BehaviourControllable;
import game.capabilities.GameAbilities;

import java.util.Map;
import java.util.TreeMap;

/**
 * Abstract base class for non-player actors that select actions via a
 * prioritised collection of {@link Behaviour}s each turn.
 * Extracts the common {@code playTurn} logic shared by creatures like
 * {@link Undead} and {@link Slime}, upholding the DRY principle.
 *
 * <p>Subclasses register their behaviours in their constructor through
 * {@link #addBehaviour(int, Behaviour)} with lower integer keys representing
 * higher priority.</p>
 *
 * <p>The alarm system uses {@link #removeBehaviour(int)} to cleanly inject
 * and remove the chase override at priority 0 without modifying
 * {@code playTurn} logic — upholding OCP.</p>
 *
 * @author Yong Leng Foong
 * @version 1.3
 */
public abstract class BehaviouralActor extends GameCharacter implements BehaviourControllable {

    /**
     * Ordered map of behaviours keyed by priority (lower = higher priority).
     */
    protected final Map<Integer, Behaviour<Actor, Action>> behaviours = new TreeMap<>();

    /**
     * Constructs a BehaviouralActor with the given attributes and inventory.
     *
     * @param name the name of the actor
     * @param displayChar the character used to display the actor on the map
     * @param hitPoints the starting hit points
     * @param inventory the inventory this actor carries
     */
    public BehaviouralActor(String name, char displayChar, int hitPoints, Inventory inventory) {
        super(name, displayChar, hitPoints, inventory);
        enableAbility(GameAbilities.IS_BEHAVIOURAL);
    }

    /**
     * Registers a behaviour at the given priority.
     * Lower numbers run first. Overwrites any existing behaviour at that priority.
     *
     * @param priority  the priority key (lower = higher priority)
     * @param behaviour the behaviour to register
     */
    @Override
    public void addBehaviour(int priority, Behaviour<Actor, Action> behaviour) {
        behaviours.put(priority, behaviour);
    }

    /**
     * Removes the behaviour registered at the given priority, if any.
     * Called by {@link game.alarm.AlarmListener} implementors to remove the
     * alarm chase override when the alarm expires.
     *
     * @param priority the priority key to remove
     */
    @Override
    public void removeBehaviour(int priority) {
        behaviours.remove(priority);
    }

    /**
     * Iterates through prioritised behaviours and returns the first valid action.
     * Falls back to {@link DoNothingAction} if no behaviour produces an action.
     *
     * @param actions collection of possible actions
     * @param lastAction the action taken last turn
     * @param map the current game map
     * @param display the display for output
     * @return the action to perform this turn
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        for (Behaviour<Actor, Action> behaviour : behaviours.values()) {
            Action action = behaviour.operate(this, map.locationOf(this));
            if (action != null) {
                return action;
            }
        }
        return new DoNothingAction();
    }
}